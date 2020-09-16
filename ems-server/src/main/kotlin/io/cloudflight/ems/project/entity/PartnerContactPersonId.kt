package io.cloudflight.ems.project.entity

import io.cloudflight.ems.api.project.dto.PartnerContactPersonType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class PartnerContactPersonId (

    @Column(name = "partner_id", nullable = false)
    val partnerId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: PartnerContactPersonType

) : Serializable
