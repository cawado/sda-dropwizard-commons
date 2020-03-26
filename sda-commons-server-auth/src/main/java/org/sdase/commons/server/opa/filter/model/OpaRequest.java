package org.sdase.commons.server.opa.filter.model;

public class OpaRequest {

  private OpaInput input;

  public OpaRequest() {
    // nothing here
  }

  OpaRequest(OpaInput input) {
    this.input = input;
  }

  public OpaInput getInput() {
    return input;
  }

  public OpaRequest setInput(OpaInput input) {
    this.input = input;
    return this;
  }

  public static OpaRequestBuilder builder() {
    return new OpaRequestBuilder();
  }

  @Override
  public String toString() {
    return "OpaRequest [input=" + input + "]";
  }
}
