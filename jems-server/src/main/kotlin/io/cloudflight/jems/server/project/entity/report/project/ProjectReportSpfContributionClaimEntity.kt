package io.cloudflight.jems.server.project.entity.report.project

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_spf_contribution_claim")
class ProjectReportSpfContributionClaimEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectReportEntity,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    val programmeFund: ProgrammeFundEntity?,

    var sourceOfContribution: String?,

    @Enumerated(EnumType.STRING)
    var legalStatus: ProjectPartnerContributionStatus?,

    val applicationFormPartnerContributionId: Long?,

    @field:NotNull
    val amountFromAf: BigDecimal,

    @field:NotNull
    var currentlyReported: BigDecimal,

    @field:NotNull
    val previouslyReported: BigDecimal,


    )

