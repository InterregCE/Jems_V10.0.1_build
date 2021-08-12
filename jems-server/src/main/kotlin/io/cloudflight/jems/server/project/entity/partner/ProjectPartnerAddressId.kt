package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectPartnerAddressId(

    @Column(name = "partner_id")
    @field:NotNull
    val partnerId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectPartnerAddressType

) : Serializable
