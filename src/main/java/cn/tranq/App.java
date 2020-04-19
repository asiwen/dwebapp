package cn.tranq;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Hello world!
 *
 */
public class App extends Application<AppConfig>
{
    public static void main( String[] args ) throws Exception
    {
        new App().run(args);
    }

    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {
        System.out.println( "Hello World!" );
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.getObjectMapper().registerSubtypes(WebAppServerFactory.class);
    }
}
