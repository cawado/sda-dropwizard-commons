package org.sdase.commons.server.opa.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotNull;

/** Configuration for requesting OPA PDP. */
@SuppressWarnings("UnusedReturnValue")
public class OpaConfig {

  public static final String WILDCARD_PARAMETER = "*";

  /** flag if OPA is disabled (for testing) */
  private boolean disableOpa;

  /** base url where to find the OPA */
  private String baseUrl = "http://localhost:8181";

  /**
   * dot-separated package name as defined in the policy
   *
   * <p>The package name is reformatted as part of the URL. Test {@code my.policy} becomes {@code
   * my/policy}
   */
  @NotNull private String policyPackage = "";

  /** readTimeout for opa requests in milliseconds */
  private int readTimeout = 500;

  /**
   * include the specified request-header as opa-parameter, if present. If only an asterix "*" is
   * set, all headers wil be submitted. (DEFAULT)
   */
  @JsonProperty("include-header-parameter")
  private List<String> includeHeaderParameter = Arrays.asList(WILDCARD_PARAMETER);

  /**
   * include the specified request-body-parameter as opa-parameter, if the request is a POST or a
   * PUT request and MediaType is JSON
   */
  @JsonProperty("include-body-parameter")
  private List<String> includeBodyParameter;

  /** include the specified request-queries as opa-parameter, if present */
  @JsonProperty("include-query-parameter")
  private List<String> includeQueryParameter;

  /** if false the path will not be part of the opa-request */
  private boolean includePath = true;

  public boolean isDisableOpa() {
    return disableOpa;
  }

  public OpaConfig setDisableOpa(boolean disableOpa) {
    this.disableOpa = disableOpa;
    return this;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public OpaConfig setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  public String getPolicyPackage() {
    return policyPackage;
  }

  public OpaConfig setPolicyPackage(String policyPackage) {
    this.policyPackage = policyPackage;
    return this;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public OpaConfig setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }

  public String getPolicyPackagePath() {
    return policyPackage.replaceAll("\\.", "/").trim();
  }

  public List<String> getIncludeHeaderParameter() {
    return includeHeaderParameter;
  }

  public OpaConfig setIncludeHeaderParameter(List<String> includeHeaderParameter) {
    this.includeHeaderParameter = includeHeaderParameter;
    return this;
  }

  public OpaConfig addIncludeHeaderParameter(String param) {
    if (this.includeHeaderParameter == null
        || (this.includeHeaderParameter.size() == 1
            && this.includeHeaderParameter.get(0).equals(WILDCARD_PARAMETER))) {
      this.includeHeaderParameter = new ArrayList<>();
    }
    this.includeHeaderParameter.add(param);
    return this;
  }

  public List<String> getIncludeBodyParameter() {
    return includeBodyParameter;
  }

  public OpaConfig setIncludeBodyParameter(List<String> includeBodyParameter) {
    this.includeBodyParameter = includeBodyParameter;
    return this;
  }

  public OpaConfig addIncludeBodyParameter(String param) {
    if (this.includeBodyParameter == null) {
      this.includeBodyParameter = new ArrayList<>();
    }
    this.includeBodyParameter.add(param);
    return this;
  }

  public List<String> getIncludeQueryParameter() {
    return includeQueryParameter;
  }

  public OpaConfig setIncludeQueryParameter(List<String> includeQueryParameter) {
    this.includeQueryParameter = includeQueryParameter;
    return this;
  }

  public OpaConfig addIncludeQueryParameter(String param) {
    if (this.includeQueryParameter == null) {
      this.includeQueryParameter = new ArrayList<>();
    }
    this.includeQueryParameter.add(param);
    return this;
  }

  public boolean isIncludePath() {
    return includePath;
  }

  public OpaConfig setIncludePath(boolean includePath) {
    this.includePath = includePath;
    return this;
  }

  @Override
  public String toString() {
    return "OpaConfig ["
        + "baseUrl="
        + baseUrl
        + ", disableOpa="
        + disableOpa
        + ", includeBodyParameter="
        + includeBodyParameter
        + ", includeHeaderParameter="
        + includeHeaderParameter
        + ", includePath="
        + includePath
        + ", includeQueryParameter="
        + includeQueryParameter
        + ", policyPackage="
        + policyPackage
        + ", readTimeout="
        + readTimeout
        + "]";
  }
}
