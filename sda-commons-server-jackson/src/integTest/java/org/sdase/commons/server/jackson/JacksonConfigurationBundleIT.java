package org.sdase.commons.server.jackson;

import org.sdase.commons.server.jackson.test.JacksonConfigurationTestApp;
import org.sdase.commons.server.jackson.test.PersonResource;
import io.dropwizard.Configuration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.Map;

public class JacksonConfigurationBundleIT {

   @ClassRule
   public static final DropwizardAppRule<Configuration> DW = new DropwizardAppRule<>(
         JacksonConfigurationTestApp.class, ResourceHelpers.resourceFilePath("test-config.yaml"));

   @Test
   public void shouldGetJohnDoe() {
      PersonResource johnny = DW.client()
            .target("http://localhost:" + DW.getLocalPort()).path("/jdoe")
            .request(MediaType.APPLICATION_JSON)
            .get(PersonResource.class);

      Assertions.assertThat(johnny)
            .extracting(
                  p -> p.getSelf().getHref(),
                  PersonResource::getFirstName,
                  PersonResource::getLastName,
                  PersonResource::getNickName
            )
            .containsExactly(
                  "http://localhost:" + DW.getLocalPort() + "/jdoe",
                  "John",
                  "Doe",
                  "Johnny"
            );
   }

   @Test
   public void shouldNotRenderOmittedFields() {
      Map<String, Object> johnny = DW.client()
            .target("http://localhost:" + DW.getLocalPort()).path("/jdoe")
            .queryParam("fields", "nickName")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<Map<String, Object>>() {});

      Assertions.assertThat(johnny)
            .containsKeys("_links", "nickName")
            .doesNotContainKeys("firstName", "lastName");
   }

   @Test
   public void shouldFilterField() {
      PersonResource johnny = DW.client()
            .target("http://localhost:" + DW.getLocalPort()).path("/jdoe")
            .queryParam("fields", "nickName")
            .request(MediaType.APPLICATION_JSON)
            .get(PersonResource.class);

      Assertions.assertThat(johnny)
            .extracting(
                  p -> p.getSelf().getHref(),
                  PersonResource::getFirstName,
                  PersonResource::getLastName,
                  PersonResource::getNickName
            )
            .containsExactly(
                  "http://localhost:" + DW.getLocalPort() + "/jdoe",
                  null,
                  null,
                  "Johnny"
            );
   }

   @Test
   public void shouldFilterFieldsByMultipleParams() {
      PersonResource johnny = DW.client()
            .target("http://localhost:" + DW.getLocalPort()).path("/jdoe")
            .queryParam("fields", "firstName")
            .queryParam("fields", "lastName")
            .request(MediaType.APPLICATION_JSON)
            .get(PersonResource.class);

      Assertions.assertThat(johnny)
            .extracting(
                  p -> p.getSelf().getHref(),
                  PersonResource::getFirstName,
                  PersonResource::getLastName,
                  PersonResource::getNickName
            )
            .containsExactly(
                  "http://localhost:" + DW.getLocalPort() + "/jdoe",
                  "John",
                  "Doe",
                  null
            );
   }

   @Test
   public void shouldFilterFieldsBySingleParams() {
      PersonResource johnny = DW.client()
            .target("http://localhost:" + DW.getLocalPort()).path("/jdoe")
            .queryParam("fields", "firstName, lastName")
            .request(MediaType.APPLICATION_JSON)
            .get(PersonResource.class);

      Assertions.assertThat(johnny)
            .extracting(
                  p -> p.getSelf().getHref(),
                  PersonResource::getFirstName,
                  PersonResource::getLastName,
                  PersonResource::getNickName
            )
            .containsExactly(
                  "http://localhost:" + DW.getLocalPort() + "/jdoe",
                  "John",
                  "Doe",
                  null
            );
   }
}