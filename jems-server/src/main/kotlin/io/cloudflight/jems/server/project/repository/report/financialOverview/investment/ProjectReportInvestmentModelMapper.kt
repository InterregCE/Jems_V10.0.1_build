package io.cloudflight.jems.server.project.repository.report.financialOverview.investment

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdownLine

fun PartnerReportInvestmentEntity.toModel() = ExpenditureInvestmentBreakdownLine(
    reportInvestmentId = id,
    investmentId = investmentId,
    investmentNumber = investmentNumber,
    title = translatedValues.extractField { it.title },
    workPackageNumber = workPackageNumber,
    totalEligibleBudget = total,
    previouslyReported = previouslyReported,
    currentReport = current,
)
