package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing

data class ExpenditureCoFinancingCurrentWithReIncluded(
    val current: ReportExpenditureCoFinancingColumn,
    val currentReIncluded: ReportExpenditureCoFinancingColumn,
)
