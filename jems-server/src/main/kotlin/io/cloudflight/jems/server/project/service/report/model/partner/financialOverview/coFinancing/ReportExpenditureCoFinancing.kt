package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing

data class ReportExpenditureCoFinancing(
    val totalsFromAF: ReportExpenditureCoFinancingColumn,
    val currentlyReported: ReportExpenditureCoFinancingColumn,
    val currentlyReportedReIncluded: ReportExpenditureCoFinancingColumn,
    val totalEligibleAfterControl: ReportExpenditureCoFinancingColumn,
    val previouslyReported: ReportExpenditureCoFinancingColumn,
    val previouslyReportedParked: ReportExpenditureCoFinancingColumn,
    val previouslyPaid: ReportExpenditureCoFinancingColumn,
)
