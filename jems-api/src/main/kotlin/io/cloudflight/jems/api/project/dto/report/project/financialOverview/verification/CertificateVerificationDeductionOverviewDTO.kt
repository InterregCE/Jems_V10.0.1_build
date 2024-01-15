package io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

data class CertificateVerificationDeductionOverviewDTO(
    val partnerReportNumber: Int,
    val partnerNumber: Int,
    val partnerRole: ProjectPartnerRoleDTO,
    val deductionOverview: VerificationDeductionOverviewDTO
)
