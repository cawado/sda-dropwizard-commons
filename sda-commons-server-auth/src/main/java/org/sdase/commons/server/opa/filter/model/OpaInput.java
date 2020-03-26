package org.sdase.commons.server.opa.filter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * TODO: Discussion: rename parameter to clarify the meaning - headers => headerParameter - bodies
 * => bodyParameter - queries => queryParameter
 */
@JsonInclude(Include.NON_NULL)
public class OpaInput {

  /** trace token to be able to find opa debug */
  private String trace;

  /** JWT received with the request */
  private String jwt;

  /** url path to the resource without base url */
  private String[] path;

  /** HTTP Method */
  private String httpMethod;

  /**
   * Additional, optional headers that get passed to the OPA service, if configured Default: all
   * parameters
   */
  private Map<String, List<String>> headers;

  /**
   * Additional, optional body parameters that get passed to the OPA service, if configured Default:
   * all parameters
   */
  private Map<String, Object> bodies;

  /**
   * Additional, optional headers that get passed to the OPA service, if configured Default: all
   * parameters
   */
  private Map<String, List<String>> queries;

  public OpaInput() {
    // nothing here, just for Jackson
  }

  public String getJwt() {
    return jwt;
  }

  public OpaInput setJwt(String jwt) {
    this.jwt = jwt;
    return this;
  }

  public String[] getPath() {
    return path;
  }

  public OpaInput setPath(String[] path) {
    this.path = path;
    return this;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public OpaInput setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
    return this;
  }

  public String getTrace() {
    return trace;
  }

  public OpaInput setTrace(String trace) {
    this.trace = trace;
    return this;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public OpaInput setHeaders(Map<String, List<String>> headerParameter) {
    this.headers = headerParameter;
    return this;
  }

  public Map<String, Object> getBodies() {
    return bodies;
  }

  public OpaInput setBodies(Map<String, Object> bodyParameter) {
    this.bodies = bodyParameter;
    return this;
  }

  public Map<String, List<String>> getQueries() {
    return queries;
  }

  public OpaInput setQueries(Map<String, List<String>> queryParameter) {
    this.queries = queryParameter;
    return this;
  }

  @Override
  public String toString() {
    return "OpaInput ["
        + "bodyParameter="
        + bodies
        + ", headerParameter="
        + headers
        + ", httpMethod="
        + httpMethod
        + ", jwt="
        + jwt
        + ", path="
        + Arrays.toString(path)
        + ", queryParameter="
        + queries
        + ", trace="
        + trace
        + "]";
  }
}
