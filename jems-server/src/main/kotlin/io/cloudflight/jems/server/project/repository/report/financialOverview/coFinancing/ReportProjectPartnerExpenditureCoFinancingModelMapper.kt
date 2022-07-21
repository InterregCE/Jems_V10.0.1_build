package io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn

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
    previouslyReported = ReportExpenditureCoFinancingColumn(
        funds = coFinancing.associateBy({ it.programmeFund?.id }, { it.previouslyReported }),
        partnerContribution = partnerContributionPreviouslyReported,
        publicContribution = publicContributionPreviouslyReported,
        automaticPublicContribution = automaticPublicContributionPreviouslyReported,
        privateContribution = privateContributionPreviouslyReported,
        sum = sumPreviouslyReported,
    ),
)
