package org.sdase.commons.server.openapi;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Partner")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = NaturalPersonResource.class, name = "naturalPerson") })
public abstract class PartnerResource {

   @Schema(description = "The type of partner, controls the available properties.", required = true, allowableValues = "naturalPerson", example = "naturalPerson")
   private String type;

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }
}
