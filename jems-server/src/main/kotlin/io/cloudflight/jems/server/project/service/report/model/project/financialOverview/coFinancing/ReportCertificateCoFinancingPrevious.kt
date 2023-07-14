package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

data class ReportCertificateCoFinancingPrevious(
    val previouslyReported: ReportCertificateCoFinancingColumn,
    val previouslyVerified: ReportCertificateCoFinancingColumn,
) {
    fun getAllFundIds() = previouslyReported.funds.keys.filterNotNull() union
            previouslyVerified.funds.keys.filterNotNull()
}
