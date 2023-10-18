package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.verification.financialOverview.ProjectReportVerificationCertificateContributionOverviewEntity
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerCertificateFundSplit


fun FinancingSourceBreakdownLine.toEntity(partnerReport: ProjectPartnerReportEntity) =
    ProjectReportVerificationCertificateContributionOverviewEntity(
        partnerReport = partnerReport,
        programmeFund = null,
        fundValue = null,
        partnerContribution = this.partnerContribution,
        publicContribution = this.publicContribution,
        automaticPublicContribution = this.automaticPublicContribution,
        privateContribution = this.privateContribution,
        total = this.total
    )


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

fun List<FinancingSourceBreakdownSplitLine>.toEntities(
    partnerReport: ProjectPartnerReportEntity,
    fundsResolver: (fundId: Long) -> ProgrammeFundEntity
) = map {
    ProjectReportVerificationCertificateContributionOverviewEntity(
        partnerReport = partnerReport,
        programmeFund = fundsResolver.invoke(it.fundId),
        fundValue = it.value,
        partnerContribution = it.partnerContribution,
        publicContribution = it.publicContribution,
        automaticPublicContribution = it.automaticPublicContribution,
        privateContribution = it.privateContribution,
        total = it.total
    )
}

fun List<ProjectReportVerificationCertificateContributionOverviewEntity>.toModel() = map {
    PartnerCertificateFundSplit(
        partnerReportId = it.partnerReport.id,
        partnerId = it.partnerReport.partnerId,
        fundId = it.programmeFund!!.id,
        value = it.fundValue!!,

        defaultPartnerContribution = it.partnerContribution,
        defaultOfWhichPublic = it.publicContribution,
        defaultOfWhichAutoPublic = it.automaticPublicContribution,
        defaultOfWhichPrivate = it.privateContribution,

        total = it.total,
    )
}
