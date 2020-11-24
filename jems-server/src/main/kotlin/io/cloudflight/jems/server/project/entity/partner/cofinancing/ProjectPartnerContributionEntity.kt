package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_contribution")
data class ProjectPartnerContributionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val partnerId: Long,

    val name: String? = null,

    @Enumerated(EnumType.STRING)
    val status: ProjectPartnerContributionStatus? = null,

    @field:NotNull
    val amount: BigDecimal

)
