package fr.istic.tlc.evolvablebydesign;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Value;

@Value
public class HypermediaRepresentation<T> {

  private final T resource;
  private final List<Link> links;

  public static <A> Builder<A> empty() {
    return new Builder<>(null);
  }

  public static <A> Builder<A> of (A resource) {
    return new Builder<>(resource);
  }

  public static class Builder<T> {
    private final T resource;
    private final List<Link> links;

    private Builder(T resource) {
      this.resource = resource;
      this.links = new LinkedList<>();
    }

    public Builder<T> withLink(String relation) {
      return this.withLink(relation, () -> true);
    }

    public Builder<T> withLink(String relation, BooleanSupplier isPresent) {
      return this.withLink(relation, null, isPresent);
    }

    public Builder<T> withLink(String relation, Map<String, Object> parameters) {
      return this.withLink(relation, parameters, () -> true);
    }

    public Builder<T> withLink(String relation, Map<String, Object> parameters, BooleanSupplier isPresent) {
      if (relation != null && isPresent.getAsBoolean()) {
        if (parameters == null || parameters.isEmpty()) {
          this.links.add(new Link.Simple(relation));
        } else {
          this.links.add(new Link.WithParameters(relation, parameters));
        }
      }
      return this;
    }

    public HypermediaRepresentation<T> build() {
      return new HypermediaRepresentation<>(this.resource, Collections.unmodifiableList(this.links));
    }
  }

  public static class Serializer extends JsonSerializer<HypermediaRepresentation> {

    @Override
    public Class<HypermediaRepresentation> handledType() {
      return HypermediaRepresentation.class;
    }

    @Override
    public void serialize(HypermediaRepresentation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (value.resource instanceof Collection) {
        System.err.println("Can not create an hypermedia representation of an array. Links can not be added into an array. Please transform the array into an object.");

        provider.defaultSerializeValue(value.resource, gen);
      } else {
        final ObjectMapper objectMapper = JacksonConfiguration.objectMapper();

        final ObjectNode mainObject = value.resource != null
          ? objectMapper.convertValue(value.resource, ObjectNode.class)
          : new ObjectNode(JsonNodeFactory.instance);
        final ArrayNode linksArray = mainObject.putArray("_links");
        value.links.forEach(link -> linksArray.add(objectMapper.valueToTree(link)));

        mainObject.serialize(gen, provider);
      }
    }
  }

}