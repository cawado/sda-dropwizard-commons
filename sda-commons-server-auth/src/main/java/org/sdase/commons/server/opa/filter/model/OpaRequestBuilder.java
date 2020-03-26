package org.sdase.commons.server.opa.filter.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import org.sdase.commons.server.opa.config.OpaConfig;
import org.sdase.commons.shared.tracing.RequestTracing;

/** OpaRequestBuilder */
public class OpaRequestBuilder {

  private OpaConfig config;
  private ContainerRequestContext context;
  private ObjectMapper mapper;
  private String jwt;

  public OpaRequestBuilder config(OpaConfig config) {
    this.config = config;
    return this;
  }

  public OpaRequestBuilder mapper(ObjectMapper mapper) {
    this.mapper = mapper;
    return this;
  }

  public OpaRequestBuilder context(ContainerRequestContext context) {
    this.context = context;
    return this;
  }

  public OpaRequestBuilder jwt(String jwt) {
    this.jwt = jwt;
    return this;
  }

  OpaRequestBuilder() {
    /* creator security */
  }

  public OpaRequest build() {
    assert config != null;
    assert context != null;
    assert mapper != null;

    UriInfo uriInfo = context.getUriInfo();
    return new OpaRequest(
        new OpaInput()
            .setHttpMethod(context.getMethod())
            .setTrace(context.getHeaderString(RequestTracing.TOKEN_HEADER))
            .setJwt(jwt)
            // Add path:
            .setPath(
                uriInfo.getPathSegments().stream().map(PathSegment::getPath).toArray(String[]::new))
            // Add headers with lowercase keys:
            // Headers are currently passed to OPA as read by the framework. There might be an issue
            // with multivalued headers. The representation differs depending on how the client
            // sends the headers. It might be a list with values, or one entry separated with a
            // separator, for example ',' or a combination of both.
            .setHeaders(lowerAndFilter(context.getHeaders(), config.getIncludeHeaderParameter()))
            // Add queries with lowercase keys:
            .setQueries(
                lowerAndFilter(uriInfo.getQueryParameters(), config.getIncludeQueryParameter()))
            // Add body parameter:
            .setBodies(fromBody(context.getEntityStream(), config.getIncludeBodyParameter())));
  }

  private Map<String, Object> fromBody(InputStream entityStream, List<String> include) {
    try {
      return lowerAndFilter(
          mapper.readValue(entityStream, new TypeReference<Map<String, Object>>() {}), include);
    } catch (Exception e) {
      // if any error occurs return nothing
      return null;
    }
  }

  private static <T> Map<String, T> lowerAndFilter(Map<String, T> values, List<String> include) {
    if (values == null || values.isEmpty() || include == null || include.isEmpty()) {
      return null;
    }
    if (include.size() == 1 && include.get(0).equalsIgnoreCase(OpaConfig.WILDCARD_PARAMETER)) {
      return values.entrySet().stream()
          .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue));
    }

    return include.stream()
        .map(String::toLowerCase)
        .filter(key -> findByLowerKey(key, values) != null)
        .collect(Collectors.toMap(key -> key, key -> findByLowerKey(key, values)));
  }

  static <T> T findByLowerKey(String keyToFind, Map<String, T> values) {
    return values.entrySet().stream()
        .filter(entry -> entry.getKey().equalsIgnoreCase(keyToFind))
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse(null);
  }
}
