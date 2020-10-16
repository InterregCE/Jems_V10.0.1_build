package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class ProjectPartnerAddressId (

    @Column(name = "partner_id", nullable = false)
    val partnerId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProjectPartnerAddressType

) : Serializable
