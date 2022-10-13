package io.cloudflight.jems.server.project.repository.report.financialOverview.investment

import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureInvestmentEntity
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary


fun ReportProjectPartnerExpenditureInvestmentEntity.toModel() = ExpenditureInvestmentBreakdownLine(
    investmentId = investmentId,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackageNumber,
    totalEligibleBudget = total,
    previouslyReported = previouslyReported,
    currentReport = current
)

fun ReportProjectPartnerExpenditureInvestmentEntity.toInvestmentSummary() = InvestmentSummary(
    id = investmentId,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackageNumber
)
