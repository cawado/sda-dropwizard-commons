package org.sdase.commons.server.openapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.EmbeddedResource;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

@Resource
@Schema(name = "House")
public class HouseResource {
   @Link
   @Schema(description = "Link relation 'self': The HAL link referencing this file.")
   private HALLink self;

   @Link("partners")
   @Schema(description = "list of partners that live in the house (Link)")
   private List<HALLink> partnersLink = new ArrayList<>();

   @EmbeddedResource
   @Schema(description = "list of partners that live in the house (Embedded)")
   private final List<PartnerResource> partners = new ArrayList<>();

   @Link("animals")
   @Schema(description = "list of animals that live in the house (Link)")
   private List<HALLink> animalsLink = new ArrayList<>();

   @EmbeddedResource
   @Schema(description = "list of animals that live in the house (Embedded)")
   private final List<AnimalResource> animals = new ArrayList<>();

   @JsonCreator
   public HouseResource(@JsonProperty("partners") List<PartnerResource> partners,
         @JsonProperty("animals") List<AnimalResource> animals, HALLink self) {

      this.partners.addAll(partners);
      this.animals.addAll(animals);
      this.self = self;
   }

   public HALLink getSelf() {
      return self;
   }

   public List<HALLink> getPartnersLink() {
      return partnersLink;
   }

   public List<PartnerResource> getPartners() {
      return partners;
   }

   public List<HALLink> getAnimalsLink() {
      return animalsLink;
   }

   public List<AnimalResource> getAnimals() {
      return animals;
   }
}
