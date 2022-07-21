package io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing

data class ReportExpenditureCoFinancing(
    val totalsFromAF: ReportExpenditureCoFinancingColumn,
    val currentlyReported: ReportExpenditureCoFinancingColumn,
    val previouslyReported: ReportExpenditureCoFinancingColumn,
)
