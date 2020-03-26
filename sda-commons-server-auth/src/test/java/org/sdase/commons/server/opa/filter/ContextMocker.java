package org.sdase.commons.server.opa.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/** ContextMocker */
public class ContextMocker {

  private ContainerRequestContext context;
  private SecurityContext securityContext;
  private UriInfo uriInfo;

  public ContextMocker() {
    this.context = mock(ContainerRequestContext.class);
    this.securityContext = mock(SecurityContext.class);
    this.uriInfo = mock(UriInfo.class);

    doReturn(securityContext).when(context).getSecurityContext();
    doReturn(uriInfo).when(context).getUriInfo();
  }

  public ContextMocker withHeaderParameter(MultivaluedMap<String, String> headerParams) {
    doReturn(headerParams).when(context).getHeaders();
    when(context.getHeaderString(anyString()))
        .thenAnswer(args -> headerParams.getFirst(args.getArgument(0)));
    return this;
  }

  public ContextMocker withQueryParameter(MultivaluedMap<String, String> queryParams) {
    doReturn(queryParams).when(uriInfo).getQueryParameters();
    return this;
  }

  public ContextMocker withBodyParameter(Map<String, String> bodyParams) {
    String bodyString =
        "{"
            + bodyParams.entrySet().stream()
                .map(entry -> String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","))
            + "}";
    doReturn(new ByteArrayInputStream(bodyString.getBytes())).when(context).getEntityStream();
    return this;
  }

  public ContextMocker withPath(String path) {
    return withPathSegments(path.split("/"));
  }

  public ContextMocker withPathSegments(List<String> path) {
    return withPathSegments(path.toArray(new String[path.size()]));
  }

  public ContextMocker withPathSegments(String... path) {
    List<PathSegment> segments =
        Arrays.stream(path).map(ContextMocker::createSegment).collect(Collectors.toList());

    doReturn(segments).when(uriInfo).getPathSegments();
    return this;
  }

  public ContainerRequestContext build() {
    return context;
  }

  private static PathSegment createSegment(String path) {
    return new PathSegment() {
      @Override
      public String getPath() {
        return path;
      }

      @Override
      public MultivaluedMap<String, String> getMatrixParameters() {
        return null;
      }
    };
  }
}
