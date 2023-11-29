package io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

data class CorrectionAvailablePartnerDTO(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerDisabled: Boolean,

    val availableReports: List<CorrectionAvailablePartnerReportDTO>,
    val availableFtls: List<CorrectionAvailableFtlsDTO>,
)
