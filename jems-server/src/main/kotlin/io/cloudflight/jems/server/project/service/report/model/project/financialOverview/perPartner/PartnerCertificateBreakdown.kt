package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner

data class PartnerCertificateBreakdown(
    val certificates: List<PartnerCertificateBreakdownLine>,
    val total: PartnerCertificateBreakdownLine
)
