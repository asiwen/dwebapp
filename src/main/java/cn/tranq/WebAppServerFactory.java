package cn.tranq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.ContextRoutingHandler;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.AbstractServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;

@JsonTypeName("webapp")
public class WebAppServerFactory extends AbstractServerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServerFactory.class);

    @Valid
    @NotNull
    private ConnectorFactory connector = HttpConnectorFactory.application();

    @NotEmpty
    private String applicationContextPath = "/application";

    @NotEmpty
    private String adminContextPath = "/admin";

    @NotEmpty
    private String webAppContextPath = "/myweb";

    @JsonProperty
    public ConnectorFactory getConnector() {
        return connector;
    }

    @JsonProperty
    public void setConnector(ConnectorFactory factory) {
        this.connector = factory;
    }

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(String contextPath) {
        this.applicationContextPath = contextPath;
    }

    @JsonProperty
    public String getAdminContextPath() {
        return adminContextPath;
    }

    @JsonProperty
    public void setAdminContextPath(String contextPath) {
        this.adminContextPath = contextPath;
    }

    @JsonProperty
    public String getWebAppContextPath(){
        return this.webAppContextPath;
    }

    @JsonProperty
    public void setWebAppContextPath(String contextPath){
        this.webAppContextPath = contextPath;
    }

    @Override
    public Server build(Environment environment) {
        configure(environment);

        printBanner(environment.getName());
        final ThreadPool threadPool = createThreadPool(environment.metrics());
        final Server server = buildServer(environment.lifecycle(), threadPool);

        final Handler applicationHandler = createAppServlet(server,
                environment.jersey(),
                environment.getObjectMapper(),
                environment.getValidator(),
                environment.getApplicationContext(),
                environment.getJerseyServletContainer(),
                environment.metrics());

        final Handler adminHandler = createAdminServlet(server,
                environment.getAdminContext(),
                environment.metrics(),
                environment.healthChecks());

        final Connector conn = connector.build(server,
                environment.metrics(),
                environment.getName(),
                null);

        server.addConnector(conn);

        final ContextRoutingHandler routingHandler = new ContextRoutingHandler(ImmutableMap.of(
                applicationContextPath, applicationHandler,
                adminContextPath, adminHandler,
                webAppContextPath, createWebAppContext(server)
        ));
        final Handler gzipHandler = buildGzipHandler(routingHandler);
        server.setHandler(addStatsHandler(addRequestLog(server, gzipHandler, environment.getName())));

        return server;
    }

    private Handler createWebAppContext(Server server){
        WebAppContext context = new WebAppContext(new PathResource( new File("webapp")), webAppContextPath);
        context.setDescriptor("webapp/WEB-INF/web.xml");
        context.setServer(server);

        Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
        classList.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");
        return context;
    }

    @Override
    public void configure(Environment environment) {
        LOGGER.info("Registering jersey handler with root path prefix: {}", applicationContextPath);
        environment.getApplicationContext().setContextPath(applicationContextPath);

        LOGGER.info("Registering admin handler with root path prefix: {}", adminContextPath);
        environment.getAdminContext().setContextPath(adminContextPath);
    }
}
