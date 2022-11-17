package io.cloudflight.jems.server.controllerInstitution.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "controller_institution_nuts")
class ControllerInstitutionNutsEntity(
    @EmbeddedId
    val id: ControllerInstitutionNutsId
)
