package io.cloudflight.jems.server.project.entity.report.project.identification

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Entity(name = "report_project_spending_profile")
data class ProjectReportSpendingProfileEntity(

    @EmbeddedId
    val id: ProjectReportSpendingProfileId,

    @field:NotNull
    val partnerNumber: Int,

    @field:NotNull
    val partnerAbbreviation: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val partnerRole: ProjectPartnerRole,

    val country: String?,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @field:NotNull
    var currentlyReported: BigDecimal,

    @field:NotNull
    var partnerTotalEligibleBudget: BigDecimal
)
