package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.verification.financialOverview.ProjectReportVerificationCertificateContributionOverviewEntity
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import java.math.BigDecimal


fun FinancingSourceBreakdownLine.toOverviewEntity(
    partnerReport: ProjectPartnerReportEntity,
    fund: ProgrammeFundEntity?,
    fundValue: BigDecimal?
) = ProjectReportVerificationCertificateContributionOverviewEntity(
    partnerReport = partnerReport,
    programmeFund = fund,
    fundValue =  fundValue,
    partnerContribution = this.partnerContribution,
    publicContribution = this.publicContribution,
    automaticPublicContribution = this.automaticPublicContribution,
    privateContribution = this.privateContribution,
    total = this.total
)


fun List<FinancingSourceBreakdownSplitLine>.toOverviewEntityList(
    partnerReport: ProjectPartnerReportEntity,
    availableFunds: Map<Long, ProgrammeFundEntity>
) = this.map { fundContributionSplit ->
    ProjectReportVerificationCertificateContributionOverviewEntity(
        partnerReport = partnerReport,
        programmeFund = availableFunds[fundContributionSplit.fundId]!!,
        fundValue = fundContributionSplit.value,
        partnerContribution = fundContributionSplit.partnerContribution,
        publicContribution = fundContributionSplit.publicContribution,
        automaticPublicContribution = fundContributionSplit.automaticPublicContribution,
        privateContribution = fundContributionSplit.privateContribution,
        total = fundContributionSplit.total
    )
}

fun List<ProjectReportVerificationCertificateContributionOverviewEntity?>.toSplitLineModelList() = this.map {
    FinancingSourceBreakdownSplitLine(
        fundId = it?.programmeFund!!.id,
        value = it.fundValue!!,
        partnerContribution = it.partnerContribution,
        publicContribution = it.publicContribution,
        automaticPublicContribution = it.automaticPublicContribution,
        privateContribution = it.privateContribution,
        total = it.total
    )
}