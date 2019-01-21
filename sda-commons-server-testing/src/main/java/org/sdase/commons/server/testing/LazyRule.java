package org.sdase.commons.server.testing;

import java.util.function.Supplier;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * <p>
 *   This {@link TestRule} allows to wrap another rule to defer the initialization of the rule till
 *   the rule is started the first time. This allows to initialize a rule with parameters that are
 *   only available once another rule is completely initialized. This is often required if one rule
 *   opens a random port that the other rule want to connect to.
 *
 *   The wrapped rule can be accessed via {@link LazyRule::getRule()}
 * </p>
 * <p>
 *    Example:
 * </p>
 * <pre>
 *    {@code
 *       class MyTest {
 *          private static final WireMockClassRule WIRE = new WireMockClassRule(wireMockConfig().dynamicPort());
 *          private static final LazyRule<DropwizardAppRule<AppConfiguration>> DW = new LazyRule<>(
 *                () -> new DropwizardAppRule<>(
 *                      TestApplication.class,
 *                      ResourceHelpers.resourceFilePath("test-config.yml"),
 *                      ConfigOverride.config("url", WIRE.baseUrl())));
 *
 *          @ClassRule public static final RuleChain CHAIN = RuleChain.outerRule(WIRE).around(DW);
 *       }
 *     }
 * </pre>
 */
public class LazyRule<T extends TestRule> implements TestRule {
  private Supplier<T> ruleSupplier;
  private T rule;

  public LazyRule(Supplier<T> ruleSupplier) {
    this.ruleSupplier = ruleSupplier;
  }

  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        rule = ruleSupplier.get();
        Statement statement = rule.apply(base, description);
        statement.evaluate();
      }
    };
  }

  /**
   * Provides access to the wrapped rule. Throws if the rule isn't initialized yet.
   * @return The wrapped {@link TestRule}.
   */
  public T getRule() {
    if (rule == null) {
      throw new IllegalStateException("rule not yet initialized");
    }

    return rule;
  }
}