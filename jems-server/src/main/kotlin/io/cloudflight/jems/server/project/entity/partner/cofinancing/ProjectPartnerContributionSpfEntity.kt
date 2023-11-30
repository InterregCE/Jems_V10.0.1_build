package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_contribution_spf")
data class ProjectPartnerContributionSpfEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val partnerId: Long,

    var name: String? = null,

    @Enumerated(EnumType.STRING)
    var status: ProjectPartnerContributionStatus? = null,

    @field:NotNull
    var amount: BigDecimal

)
