package io.cloudflight.jems.server.controllerInstitution.entity

import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "controller_institution_partner")
class ControllerInstitutionPartnerEntity(

    @Id
    val partnerId: Long,

    @ManyToOne(optional = false)
    @field:NotNull
    val institution: ControllerInstitutionEntity,

    @field:NotNull
    val partnerProjectId: Long,

)
