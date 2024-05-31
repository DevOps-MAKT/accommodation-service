package uns.ac.rs.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.nio.file.Files;

@Path("/photo")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
public class PhotographController {

    private static final String IMAGE_DIR = "src/main/resources/images/";

    @GET
    @Path("/{imageName}")
    @Produces({"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"})
    public Response getImage(@PathParam("imageName") String imageName) {
        try {
            File file = new File(IMAGE_DIR + imageName);
            if (file.exists()) {
                return Response.ok(Files.readAllBytes(file.toPath()))
                        .header("Content-Disposition", "inline; filename=\"" + imageName + "\"")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
