package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import java.math.BigDecimal

fun ReportProjectPartnerExpenditureCoFinancingEntity.toModel(
    coFinancing: List<ProjectPartnerReportCoFinancingEntity>,
) = ReportExpenditureCoFinancing(
    totalsFromAF = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.total }),
        partnerContribution = partnerContributionTotal,
        publicContribution = publicContributionTotal,
        automaticPublicContribution = automaticPublicContributionTotal,
        privateContribution = privateContributionTotal,
        sum = sumTotal,
    ),
    currentlyReported = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.current }),
        partnerContribution = partnerContributionCurrent,
        publicContribution = publicContributionCurrent,
        automaticPublicContribution = automaticPublicContributionCurrent,
        privateContribution = privateContributionCurrent,
        sum = sumCurrent,
    ),
    currentlyReportedReIncluded = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.currentReIncluded }),
        partnerContribution = partnerContributionCurrentReIncluded,
        publicContribution = publicContributionCurrentReIncluded,
        automaticPublicContribution = automaticPublicContributionCurrentReIncluded,
        privateContribution = privateContributionCurrentReIncluded,
        sum = sumCurrentReIncluded
    ),
    totalEligibleAfterControl = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.totalEligibleAfterControl }),
        partnerContribution = partnerContributionTotalEligibleAfterControl,
        publicContribution = publicContributionTotalEligibleAfterControl,
        automaticPublicContribution = automaticPublicContributionTotalEligibleAfterControl,
        privateContribution = privateContributionTotalEligibleAfterControl,
        sum = sumTotalEligibleAfterControl,
    ),
    previouslyReported = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyReported }),
        partnerContribution = partnerContributionPreviouslyReported,
        publicContribution = publicContributionPreviouslyReported,
        automaticPublicContribution = automaticPublicContributionPreviouslyReported,
        privateContribution = privateContributionPreviouslyReported,
        sum = sumPreviouslyReported,
    ),
    previouslyReportedParked = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyReportedParked }),
        partnerContribution = partnerContributionPreviouslyReportedParked,
        publicContribution = publicContributionPreviouslyReportedParked,
        automaticPublicContribution = automaticPublicContributionPreviouslyReportedParked,
        privateContribution = privateContributionPreviouslyReportedParked,
        sum = sumPreviouslyReportedParked,
    ),
    previouslyReportedSpf = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyReportedSpf }),
        partnerContribution = partnerContributionPreviouslyReportedSpf,
        publicContribution = publicContributionPreviouslyReportedSpf,
        automaticPublicContribution = automaticPublicContributionPreviouslyReportedSpf,
        privateContribution = privateContributionPreviouslyReportedSpf,
        sum = sumPreviouslyReportedSpf,
    ),
    previouslyValidated = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyValidated }),
        partnerContribution = partnerContributionPreviouslyValidated,
        publicContribution = publicContributionPreviouslyValidated,
        automaticPublicContribution = automaticPublicContributionPreviouslyValidated,
        privateContribution = privateContributionPreviouslyValidated,
        sum = sumPreviouslyValidated,
    ),
    previouslyPaid = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyPaid }),
        partnerContribution = BigDecimal.ZERO,
        publicContribution = BigDecimal.ZERO,
        automaticPublicContribution = BigDecimal.ZERO,
        privateContribution = BigDecimal.ZERO,
        sum = coFinancing.sumOf { it.previouslyPaid },
    ),
)

fun ReportExpenditureCoFinancingColumnWithoutFunds.toModel(
    fundsData: List<ProjectReportCumulativeFund>
) = ReportCertificateCoFinancingColumn(
    funds = fundsData.associateBy({it.reportFundId}, {it.sum}),
    partnerContribution = partnerContribution,
    publicContribution = publicContribution,
    automaticPublicContribution = automaticPublicContribution,
    privateContribution = privateContribution,
    sum = sum
)
