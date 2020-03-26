package org.sdase.commons.server.opa.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.StringTag;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sdase.commons.server.opa.config.OpaConfig;
import org.sdase.commons.server.opa.filter.model.OpaRequest;
import org.sdase.commons.server.opa.filter.model.OpaResponse;

@RunWith(MockitoJUnitRunner.class)
public class OpaAuthFilterTest {
  private ObjectMapper mapper = new ObjectMapper();

  @Mock WebTarget target;
  @Mock Invocation.Builder requestBuilder;
  @Mock Tracer tracer;

  private OpaConfig config;
  private List<String> excludePatterns;

  private ContainerRequestContext context;
  private List<String> path;
  private MultivaluedMap<String, String> queryParams;
  private Map<String, String> bodyParams;
  private MultivaluedMap<String, String> headerParams;

  private OpaAuthFilter filter;
  private MediaType mediaType;
  private OpaRequest request;

  @Before
  public void init() {
    mockUseless();
    initContext();
    initTarget();
  }

  private void initContext() {
    this.path = Arrays.asList("opa", "test", "path");

    this.queryParams =
        new MultivaluedHashMap<>(
            Map.of(
                "query1", "value1",
                "query2", "value2",
                "onlyQuery", "onlyQueryValue"));

    this.bodyParams = Map.of("onlyBody", "onlyBodyValue", "body1", "value1", "body2", "value2");

    this.headerParams =
        new MultivaluedHashMap<>(
            Map.of(
                "header1", "headerValue1",
                "header2", "headerValue2",
                "onlyHeader", "onlyHeaderValue"));

    this.context =
        new ContextMocker()
            .withPathSegments(this.path)
            .withBodyParameter(this.bodyParams)
            .withHeaderParameter(this.headerParams)
            .withQueryParameter(this.queryParams)
            .build();
  }

  @SuppressWarnings("unchecked")
  private void initTarget() {
    when(target.request(any(MediaType.class)))
        .thenAnswer(
            args -> {
              this.mediaType = args.getArgument(0);
              return this.requestBuilder;
            });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode result = jnf.objectNode();
    result.put(OpaResponse.ALLOW, true);

    when(requestBuilder.post(any(), eq(OpaResponse.class)))
        .thenAnswer(
            args -> {
              this.request = ((Entity<OpaRequest>) args.getArgument(0)).getEntity();
              return new OpaResponse().setResult(result);
            });
  }

  private void mockUseless() {
    SpanBuilder spanBuilder = mock(SpanBuilder.class);
    Span span = mock(Span.class);
    Scope scope = mock(Scope.class);
    ScopeManager scopeManager = mock(ScopeManager.class);

    when(tracer.buildSpan(anyString())).thenReturn(spanBuilder);
    when(tracer.scopeManager()).thenReturn(scopeManager);
    when(scopeManager.activate(span)).thenReturn(scope);
    when(spanBuilder.withTag(any(StringTag.class), anyString())).thenReturn(spanBuilder);
    when(spanBuilder.withTag(anyString(), anyBoolean())).thenReturn(spanBuilder);
    when(spanBuilder.start()).thenReturn(span);
  }

  @Test
  public void testDefaultFilter() throws Exception {
    this.config = new OpaConfig();
    this.filter = new OpaAuthFilter(target, config, excludePatterns, mapper, tracer);

    this.filter.filter(context);

    assertEquals(MediaType.APPLICATION_JSON_TYPE, this.mediaType);
    assertNull(this.request.getInput().getBodies());
    assertNull(this.request.getInput().getQueries());
    assertEqualsWithLowerCaseKey(this.headerParams, this.request.getInput().getHeaders());
  }

  @Test
  public void testNotPresent() {
    this.config =
        new OpaConfig()
            .addIncludeBodyParameter("not-set-parameter")
            .addIncludeHeaderParameter("not-set-parameter")
            .addIncludeQueryParameter("not-set-parameter");
    this.filter = new OpaAuthFilter(target, config, excludePatterns, mapper, tracer);
    this.filter.filter(context);

    assertNotNull(this.request.getInput().getBodies());
    assertTrue(this.request.getInput().getBodies().isEmpty());

    assertNotNull(this.request.getInput().getHeaders());
    assertTrue(this.request.getInput().getHeaders().isEmpty());

    assertNotNull(this.request.getInput().getQueries());
    assertTrue(this.request.getInput().getQueries().isEmpty());
  }

  @Test
  public void testAllWildcards() {
    this.config =
        new OpaConfig()
            .addIncludeBodyParameter(OpaConfig.WILDCARD_PARAMETER)
            .addIncludeHeaderParameter(OpaConfig.WILDCARD_PARAMETER)
            .addIncludeQueryParameter(OpaConfig.WILDCARD_PARAMETER);
    this.filter = new OpaAuthFilter(target, config, excludePatterns, mapper, tracer);

    this.filter.filter(context);

    assertEquals(MediaType.APPLICATION_JSON_TYPE, this.mediaType);
    assertNotNull(this.request.getInput().getHeaders());
    assertEqualsWithLowerCaseKey(this.headerParams, this.request.getInput().getHeaders());
    assertNotNull(this.request.getInput().getBodies());
    assertEqualsWithLowerCaseKey(this.bodyParams, this.request.getInput().getBodies());
    assertNotNull(this.request.getInput().getQueries());
    assertEqualsWithLowerCaseKey(this.queryParams, this.request.getInput().getQueries());
  }

  @Test
  public void testAllSingleParameter() {
    this.config =
        new OpaConfig()
            .addIncludeBodyParameter("onlyBody")
            .addIncludeHeaderParameter("onlyHeader")
            .addIncludeQueryParameter("onlyQuery");
    this.filter = new OpaAuthFilter(target, config, excludePatterns, mapper, tracer);

    System.out.println("Config: " + config);
    this.filter.filter(context);

    assertSingleEntry("onlyHeader", "onlyHeaderValue", this.request.getInput().getHeaders());
    assertSingleEntry("onlyBody", "onlyBodyValue", this.request.getInput().getBodies());
    assertSingleEntry("onlyQuery", "onlyQueryValue", this.request.getInput().getQueries());
  }

  @SuppressWarnings("rawtypes")
  private static <T> void assertSingleEntry(String key, String expected, Map<String, T> entries) {
    assertNotNull(entries);
    assertEquals(1, entries.size());
    // Test simple Map
    if (entries.get(key) instanceof String) {
      assertEquals(expected, entries.get(key));
      // Test multivalued Map
    } else if (entries.get(key) instanceof List) {
      assertEquals(expected, ((List) entries.get(key)).get(0));
    }
  }

  private static <T, U> void assertEqualsWithLowerCaseKey(
      Map<String, T> regular, Map<String, U> lower) {
    regular
        .entrySet()
        .forEach(entry -> assertEquals(entry.getValue(), lower.get(entry.getKey().toLowerCase())));
  }
}
