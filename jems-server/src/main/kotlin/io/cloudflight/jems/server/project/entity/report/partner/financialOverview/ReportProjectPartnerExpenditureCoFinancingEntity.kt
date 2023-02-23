package io.cloudflight.jems.server.project.entity.report.partner.financialOverview

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_expenditure_co_financing")
class ReportProjectPartnerExpenditureCoFinancingEntity(

    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull val reportEntity: ProjectPartnerReportEntity,

    @field:NotNull val partnerContributionTotal: BigDecimal,
    @field:NotNull val publicContributionTotal: BigDecimal,
    @field:NotNull val automaticPublicContributionTotal: BigDecimal,
    @field:NotNull val privateContributionTotal: BigDecimal,
    @field:NotNull val sumTotal: BigDecimal,

    @field:NotNull var partnerContributionCurrent: BigDecimal,
    @field:NotNull var publicContributionCurrent: BigDecimal,
    @field:NotNull var automaticPublicContributionCurrent: BigDecimal,
    @field:NotNull var privateContributionCurrent: BigDecimal,
    @field:NotNull var sumCurrent: BigDecimal,

    @field:NotNull var partnerContributionTotalEligibleAfterControl: BigDecimal,
    @field:NotNull var publicContributionTotalEligibleAfterControl: BigDecimal,
    @field:NotNull var automaticPublicContributionTotalEligibleAfterControl: BigDecimal,
    @field:NotNull var privateContributionTotalEligibleAfterControl: BigDecimal,
    @field:NotNull var sumTotalEligibleAfterControl: BigDecimal,

    @field:NotNull val partnerContributionPreviouslyReported: BigDecimal,
    @field:NotNull val publicContributionPreviouslyReported: BigDecimal,
    @field:NotNull val automaticPublicContributionPreviouslyReported: BigDecimal,
    @field:NotNull val privateContributionPreviouslyReported: BigDecimal,
    @field:NotNull val sumPreviouslyReported: BigDecimal,

    // Parking

    @field:NotNull var partnerContributionCurrentParked: BigDecimal,
    @field:NotNull var publicContributionCurrentParked: BigDecimal,
    @field:NotNull var automaticPublicContributionCurrentParked: BigDecimal,
    @field:NotNull var privateContributionCurrentParked: BigDecimal,
    @field:NotNull var sumCurrentParked: BigDecimal,

    @field:NotNull val partnerContributionPreviouslyReportedParked: BigDecimal,
    @field:NotNull val publicContributionPreviouslyReportedParked: BigDecimal,
    @field:NotNull val automaticPublicContributionPreviouslyReportedParked: BigDecimal,
    @field:NotNull val privateContributionPreviouslyReportedParked: BigDecimal,
    @field:NotNull val sumPreviouslyReportedParked: BigDecimal,

    @field:NotNull var partnerContributionCurrentReIncluded: BigDecimal,
    @field:NotNull var publicContributionCurrentReIncluded: BigDecimal,
    @field:NotNull var automaticPublicContributionCurrentReIncluded: BigDecimal,
    @field:NotNull var privateContributionCurrentReIncluded: BigDecimal,
    @field:NotNull var sumCurrentReIncluded: BigDecimal,

    ) : Serializable
