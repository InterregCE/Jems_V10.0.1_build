package io.cloudflight.jems.server.project.entity.report.verification.financialOverview

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull


@Entity(name = "report_project_verification_contribution_source_overview")
class ProjectReportVerificationCertificateContributionOverviewEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_report_id", referencedColumnName = "id")
    @field:NotNull
    val partnerReport: ProjectPartnerReportEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id")
    val programmeFund: ProgrammeFundEntity?,

    val fundValue: BigDecimal? = null,

    @field:NotNull
    val partnerContribution: BigDecimal,

    @field:NotNull
    val publicContribution: BigDecimal,

    @field:NotNull
    val automaticPublicContribution: BigDecimal,

    @field:NotNull
    val privateContribution: BigDecimal,

    @field:NotNull
    val total: BigDecimal
)