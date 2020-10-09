package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.project.dto.OrganizationDetailType
import io.cloudflight.jems.api.project.dto.PartnerContactPersonType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class PartnerOrganizationDetailId (
    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: OrganizationDetailType

) : Serializable
