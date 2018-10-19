package com.sdase.commons.server.jackson;

import com.sdase.commons.server.jackson.filter.JacksonFieldFilterModule;
import io.openapitools.jackson.dataformat.hal.JacksonHALModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *    Enables field filtering for the annotated resource when rendered in a response. Clients may request a lighter
 *    response containing only requested fields and HAL links or embedded resources instead of the complete model.
 * </p>
 * <p>
 *    If an annotated resource has three properties:
 * </p>
 * <pre>
 *    <code>
 *    &#64;EnableFieldFilter
 *    public class Person {
 *      private String firstName;
 *      private String surName;
 *      private String nickName;
 *      // ...
 *    }
 *    </code>
 * </pre>
 * <p>
 *    And the client requests {@code GET /person/ID?fields=firstName,surName} or
 *    {@code GET /person/ID?fields=firstName&fields=surName}, the returned Json will be:
 * </p>
 * <pre>
 *    {@code
 *    {
 *       "firstName": "John",
 *       "surName": "Doe"
 *    }
 *    }
 * </pre>
 * <p>
 *    The {@link JacksonFieldFilterModule} has to be registered after the {@link JacksonHALModule} is added to the
 *    {@link com.fasterxml.jackson.databind.ObjectMapper}. Both can be accomplished in appropriate order, using the
 *    {@link JacksonConfigurationBundle}.
 * </p>
 */
@Target({ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableFieldFilter {
}
