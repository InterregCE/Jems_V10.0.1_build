package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

data class ReportCertificateCoFinancing(
    val totalsFromAF: ReportCertificateCoFinancingColumn,
    val currentlyReported: ReportCertificateCoFinancingColumn,
    val previouslyReported: ReportCertificateCoFinancingColumn,

    val currentVerified: ReportCertificateCoFinancingColumn,
    val previouslyVerified: ReportCertificateCoFinancingColumn,

    val previouslyPaid: ReportCertificateCoFinancingColumn,
)
