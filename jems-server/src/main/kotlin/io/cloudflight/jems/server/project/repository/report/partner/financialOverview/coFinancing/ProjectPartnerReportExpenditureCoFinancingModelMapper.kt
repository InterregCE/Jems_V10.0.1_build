package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
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
    previouslyPaid = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyPaid }),
        partnerContribution = BigDecimal.ZERO,
        publicContribution = BigDecimal.ZERO,
        automaticPublicContribution = BigDecimal.ZERO,
        privateContribution = BigDecimal.ZERO,
        sum = coFinancing.sumOf { it.previouslyPaid },
    ),
)
