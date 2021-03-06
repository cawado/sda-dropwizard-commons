package org.sdase.commons.server.morphia;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.Map;
import javax.ws.rs.core.GenericType;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.sdase.commons.server.mongo.testing.MongoDbRule;
import org.sdase.commons.server.morphia.test.Config;
import org.sdase.commons.server.testing.DropwizardRuleHelper;
import org.sdase.commons.server.testing.LazyRule;

/** Tests if database health check is registered and works */
public class MorphiaBundleHealthCheckIT {

  private static final MongoDbRule MONGODB = MongoDbRule.builder().build();

  private static final LazyRule<DropwizardAppRule<Config>> DW =
      new LazyRule<>(
          () ->
              DropwizardRuleHelper.dropwizardTestAppFrom(MorphiaTestApp.class)
                  .withConfigFrom(Config::new)
                  .withRandomPorts()
                  .withConfigurationModifier(
                      c ->
                          c.getMongo()
                              .setHosts(MONGODB.getHost())
                              .setDatabase(MONGODB.getDatabase()))
                  .build());

  @ClassRule public static final RuleChain CHAIN = RuleChain.outerRule(MONGODB).around(DW);

  @Test
  public void shouldRegisterHealthCheck() {
    String healthcheckName = "mongo";
    Map<String, HealthCheckResult> healthCheck =
        DW.getRule()
            .client()
            .target("http://localhost:" + DW.getRule().getAdminPort())
            .path("/healthcheck")
            .request(APPLICATION_JSON)
            .get(new GenericType<Map<String, HealthCheckResult>>() {});
    assertThat(healthCheck).containsKey(healthcheckName);
    assertThat(healthCheck.get(healthcheckName))
        .extracting(HealthCheckResult::getHealthy)
        .isEqualTo("true");
  }

  public static class MorphiaTestApp extends Application<Config> {

    private MorphiaBundle<Config> morphiaBundle =
        MorphiaBundle.builder()
            .withConfigurationProvider(Config::getMongo)
            .withEntityScanPackage("java.lang")
            .build();

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
      bootstrap.addBundle(morphiaBundle);
    }

    @Override
    public void run(Config configuration, Environment environment) {
      // nothing to run
    }
  }

  static class HealthCheckResult {
    private String healthy;

    String getHealthy() {
      return healthy;
    }

    public HealthCheckResult setHealthy(String healthy) {
      this.healthy = healthy;
      return this;
    }
  }
}
