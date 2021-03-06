package org.sdase.commons.server.opa.testing;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.sdase.commons.server.opa.filter.model.OpaResponse;

@SuppressWarnings("WeakerAccess")
public class OpaRule extends ExternalResource {

  private static final ObjectMapper OM = new ObjectMapper();

  private final WireMockClassRule wire = new WireMockClassRule(wireMockConfig().dynamicPort());

  public static AllowBuilder onRequest(String method, String path) {
    return new StubBuilder(method, path);
  }

  private static String getJson(String[] paths) {
    try {
      return OM.writeValueAsString(paths);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Mock initialization failed");
    }
  }

  public static AllowBuilder onAnyRequest() {
    return new StubBuilder();
  }

  private static String[] splitPath(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return path.split("/");
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return RuleChain.outerRule(wire).apply(base, description);
  }

  public String getUrl() {
    return wire.baseUrl();
  }

  public void reset() {
    wire.resetAll();
  }

  @Override
  protected void before() {
    wire.start();
  }

  @Override
  protected void after() {
    wire.stop();
  }

  public void verify(int count, String httpMethod, String paths) {
    wire.verify(count, matchForVerify(httpMethod, splitPath(paths)));
  }

  public void mock(BuildBuilder builder) {
    builder.build(wire);
  }

  private RequestPatternBuilder matchForVerify(String httpMethod, String[] paths) {
    return RequestPatternBuilder.newRequestPattern(RequestMethod.POST, urlMatching("/.*"))
        .withRequestBody(matchingJsonPath("$.input.httpMethod", equalTo(httpMethod)))
        .withRequestBody(matchingJsonPath("$.input.path", equalToJson(getJson(paths))));
  }

  public interface AllowBuilder {
    FinalBuilder allow();

    FinalBuilder deny();

    BuildBuilder answer(OpaResponse response);

    BuildBuilder emptyResponse();

    BuildBuilder serverError();
  }

  public interface FinalBuilder extends BuildBuilder {

    FinalBuilder withConstraint(Object c);
  }

  public interface BuildBuilder {
    void build(WireMockClassRule wire);
  }

  public static class StubBuilder implements AllowBuilder, FinalBuilder, BuildBuilder {

    private String httpMethod;
    private String[] paths;
    private boolean allow;
    private boolean onAnyRequest = false;
    private boolean errorResponse = false;
    private boolean emptyResponse = false;
    private OpaResponse answer;
    private Object constraint;

    public StubBuilder() {
      onAnyRequest = true;
    }

    public StubBuilder(String method, String path) {
      this.httpMethod = method;
      this.paths = splitPath(path);
    }

    private static ResponseDefinitionBuilder getResponse(OpaResponse response) {
      try {
        return aResponse()
            .withStatus(200)
            .withHeader("Content-type", "application/json")
            .withBody(OM.writeValueAsBytes(response));
      } catch (JsonProcessingException exception) {
        throw new IllegalStateException("Mock initialization failed");
      }
    }

    public StubBuilder allow() {
      this.allow = true;
      return this;
    }

    public StubBuilder deny() {
      this.allow = false;
      return this;
    }

    @Override
    public BuildBuilder answer(OpaResponse answer) {
      this.answer = answer;
      return this;
    }

    @Override
    public BuildBuilder emptyResponse() {
      this.emptyResponse = true;
      return this;
    }

    @Override
    public BuildBuilder serverError() {
      this.errorResponse = true;
      return this;
    }

    @Override
    public FinalBuilder withConstraint(Object c) {
      this.constraint = c;
      return this;
    }

    @Override
    public void build(WireMockClassRule wire) {
      MappingBuilder mappingBuilder;
      if (onAnyRequest) {
        mappingBuilder = matchAnyPostUrl();
      } else {
        mappingBuilder = matchInput(httpMethod, paths);
      }

      if (errorResponse) {
        wire.stubFor(mappingBuilder.willReturn(aResponse().withStatus(500)));
        return;
      }

      if (emptyResponse) {
        wire.stubFor(mappingBuilder.willReturn(null));
        return;
      }

      OpaResponse response;
      if (answer != null) {
        response = answer;
      } else {
        ObjectNode objectNode = OM.createObjectNode();

        if (constraint != null) {
          objectNode = OM.valueToTree(constraint);
        }

        objectNode.put("allow", allow);

        response = new OpaResponse().setResult(objectNode);
      }
      wire.stubFor(mappingBuilder.willReturn(getResponse(response)));
    }

    private MappingBuilder matchAnyPostUrl() {
      return post(urlMatching("/.*"));
    }

    private MappingBuilder matchInput(String httpMethod, String[] paths) {
      return matchAnyPostUrl()
          .withRequestBody(matchingJsonPath("$.input.httpMethod", equalTo(httpMethod)))
          .withRequestBody(matchingJsonPath("$.input.path", equalToJson(getJson(paths))));
    }
  }
}
