package org.sdase.commons.server.opa.filter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sdase.commons.server.opa.config.OpaConfig;

@RunWith(MockitoJUnitRunner.class)
public class OpaRequestBuilderTest {

  private static String BODY_PARAM = "body-param";
  private static String HEADER_PARAM = "header-param";
  private static String QUERY_PARAM = "query-param";

  @Mock ContainerRequestContext context;
  @Mock UriInfo uriInfo;
  private ObjectMapper mapper = new ObjectMapper();

  private OpaConfig testConfig;

  @Before
  public void init() {
    doReturn(uriInfo).when(context).getUriInfo();
    doReturn(new MultivaluedHashMap<String, String>(Map.of(QUERY_PARAM, QUERY_PARAM + "-value")))
        .when(uriInfo)
        .getQueryParameters();
    doReturn(new MultivaluedHashMap<String, String>(Map.of(HEADER_PARAM, HEADER_PARAM + "-value")))
        .when(context)
        .getHeaders();
    String testBody = "{\"" + BODY_PARAM + "\":\"" + BODY_PARAM + "-value\"}";
    doReturn(new ByteArrayInputStream(testBody.getBytes())).when(context).getEntityStream();
  }

  @Test(expected = AssertionError.class)
  public void testNullConfig() {
    OpaRequestBuilder builder = OpaRequest.builder();
    builder.build();
  }

  @Test(expected = AssertionError.class)
  public void testNullContext() {
    OpaRequestBuilder builder = OpaRequest.builder().config(testConfig);
    builder.build();
  }

  @Test(expected = AssertionError.class)
  public void testNullMapper() {
    OpaRequestBuilder builder = OpaRequest.builder().config(testConfig).context(context);
    builder.build();
  }

  @Test
  public void testConfigAndContext() throws Exception {
    this.testConfig =
        new OpaConfig()
            .addIncludeBodyParameter(BODY_PARAM)
            .addIncludeHeaderParameter(HEADER_PARAM)
            .addIncludeQueryParameter(QUERY_PARAM);
    OpaRequest request =
        OpaRequest.builder().config(testConfig).context(context).mapper(mapper).build();

    assertEquals(BODY_PARAM + "-value", request.getInput().getBodies().get(BODY_PARAM));
    assertEquals(QUERY_PARAM + "-value", request.getInput().getQueries().get(QUERY_PARAM).get(0));
    assertEquals(HEADER_PARAM + "-value", request.getInput().getHeaders().get(HEADER_PARAM).get(0));
  }

  @Test
  public void testWildcards() throws Exception {
    this.testConfig =
        new OpaConfig()
            .addIncludeBodyParameter(OpaConfig.WILDCARD_PARAMETER)
            .addIncludeHeaderParameter(OpaConfig.WILDCARD_PARAMETER)
            .addIncludeQueryParameter(OpaConfig.WILDCARD_PARAMETER);
    OpaRequest request =
        OpaRequest.builder().config(testConfig).context(context).mapper(mapper).build();

    assertEquals(BODY_PARAM + "-value", request.getInput().getBodies().get(BODY_PARAM));
    assertEquals(QUERY_PARAM + "-value", request.getInput().getQueries().get(QUERY_PARAM).get(0));
    assertEquals(HEADER_PARAM + "-value", request.getInput().getHeaders().get(HEADER_PARAM).get(0));
  }

  @Test
  public void testEmptyConfig() throws Exception {
    this.testConfig =
        new OpaConfig()
            .setIncludeBodyParameter(null)
            .setIncludeHeaderParameter(null)
            .setIncludeQueryParameter(null);
    OpaRequest request =
        OpaRequest.builder().config(testConfig).context(context).mapper(mapper).build();

    assertNull(request.getInput().getBodies());
    assertNull(request.getInput().getQueries());
    assertNull(request.getInput().getHeaders());
  }

  @Test
  public void testJwt() throws Exception {}
}
