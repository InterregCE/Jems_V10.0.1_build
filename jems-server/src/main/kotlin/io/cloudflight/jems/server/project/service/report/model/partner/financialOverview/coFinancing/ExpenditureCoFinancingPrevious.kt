package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing

data class ExpenditureCoFinancingPrevious(
    val previous: ReportExpenditureCoFinancingColumn,
    val previousParked: ReportExpenditureCoFinancingColumn,
    val previousValidated: ReportExpenditureCoFinancingColumn,
)
