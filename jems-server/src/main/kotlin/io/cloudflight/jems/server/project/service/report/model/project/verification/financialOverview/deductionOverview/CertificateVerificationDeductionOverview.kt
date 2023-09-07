package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class CertificateVerificationDeductionOverview(
    val partnerReportNumber: Int,
    val partnerNumber: Int,
    val partnerRole: ProjectPartnerRole,
    val deductionOverview: VerificationDeductionOverview
)
