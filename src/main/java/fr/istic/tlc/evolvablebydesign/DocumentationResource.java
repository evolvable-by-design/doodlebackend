package fr.istic.tlc.evolvablebydesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class DocumentationResource {

  @OPTIONS
  @Produces(MediaType.APPLICATION_JSON)
  public Response getOpenApiDocumentation() {
    try {
      URL resource = this.getClass().getClassLoader().getResource("openapi.yaml");

      if (resource == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } else {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object yaml = yamlReader.readValue(resource, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        String documentation = jsonWriter.writeValueAsString(yaml);

        return Response.ok(documentation).build();
      }
    } catch (IOException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
