package cn.tranq.openapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;

@Path("/res")
public class ResourceApi {

    @GET
    @Path("models")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response models(){
        try {
//            InputStream  in = new FileInputStream("test.zip");
            return Response.ok(new File("test.zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=\"test.zip\"").build();
        } catch (Exception e) {
            return Response.ok("fail").build();
        }
    }
}
