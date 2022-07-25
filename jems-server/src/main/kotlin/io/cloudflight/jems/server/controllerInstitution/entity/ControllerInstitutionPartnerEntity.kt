package io.cloudflight.jems.server.controllerInstitution.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "controller_institution_partner")
class ControllerInstitutionPartnerEntity(

    @Id
    val partnerId: Long,

    @field: NotNull
    var institutionId: Long,

    @field: NotNull
    val partnerProjectId: Long

)




