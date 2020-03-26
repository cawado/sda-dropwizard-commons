package org.sdase.commons.server.opa.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Function;
import org.junit.Test;

/**
 * *
 *
 * @author Raimund S. Utz <raimund.utz@signal-idunal.de>
 * @version 1.0
 */
public class OpaConfigTest {

  @Test
  public void testDefaults() {
    OpaConfig config = new OpaConfig();

    assertNotNull(config);
    assertTrue(config.isIncludePath());
    assertFalse(config.isDisableOpa());
    assertEquals(500, config.getReadTimeout());
    assertNull(config.getIncludeBodyParameter());
    assertNull(config.getIncludeQueryParameter());
  }

  @Test
  public void testGetPolicyPackagePath() throws Exception {
    String policyUnixPath = "my/test/path";
    OpaConfig config = new OpaConfig().setPolicyPackage(policyUnixPath);
    assertEquals(policyUnixPath, config.getPolicyPackage());
    assertEquals(policyUnixPath, config.getPolicyPackagePath());

    String policyPackagePath = "my.test.path";
    config.setPolicyPackage(policyPackagePath);
    assertEquals(policyPackagePath, config.getPolicyPackage());
    assertEquals(policyUnixPath, config.getPolicyPackagePath());
  }

  @Test
  public void testAddMethods() throws Exception {
    testAddMethod(new OpaConfig()::addIncludeHeaderParameter, OpaConfig::getIncludeHeaderParameter);
    testAddMethod(new OpaConfig()::addIncludeBodyParameter, OpaConfig::getIncludeBodyParameter);
    testAddMethod(new OpaConfig()::addIncludeQueryParameter, OpaConfig::getIncludeQueryParameter);
  }

  private void testAddMethod(
      Function<String, OpaConfig> addMethod, Function<OpaConfig, List<String>> validateMethod)
      throws Exception {
    String testParam = "param";
    OpaConfig config = addMethod.apply(testParam);

    List<String> params = validateMethod.apply(config);
    assertNotNull(params);
    assertEquals(1, params.size());
    assertEquals(testParam, params.get(0));
  }
}
