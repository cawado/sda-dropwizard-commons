package org.sdase.commons.client.jersey;

import io.dropwizard.Configuration;

class JerseyClientExampleConfiguration extends Configuration {

  private String servicea;

  String getServicea() {
    return servicea;
  }

  public void setServicea(String servicea) {
    this.servicea = servicea;
  }
}
