package fr.istic.tlc.evolvablebydesign;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public class JacksonConfiguration implements ObjectMapperCustomizer {

  public void customize(ObjectMapper objectMapper) {
    final SimpleModule applicationSpecificSerializers = new SimpleModule();
    applicationSpecificSerializers.addSerializer(new Link.Serializer());
    applicationSpecificSerializers.addSerializer(new Link.Simple.Serializer());
    applicationSpecificSerializers.addSerializer(new Link.WithParameters.Serializer());
    applicationSpecificSerializers.addSerializer(new HypermediaRepresentation.Serializer());

    objectMapper.registerModule(applicationSpecificSerializers);
    objectMapper.registerModule(new ParameterNamesModule());
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.registerModule(new JavaTimeModule());

    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  public static ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    new JacksonConfiguration().customize(objectMapper);
    return objectMapper;
  }

}
