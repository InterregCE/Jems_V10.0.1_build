package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class CorrectionAvailablePartner(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRole,
    val partnerDisabled: Boolean,

    val availableReports: List<CorrectionAvailablePartnerReport>,
    val availableFtls: List<CorrectionAvailableFtls>,
)
