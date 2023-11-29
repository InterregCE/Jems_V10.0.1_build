package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

data class CorrectionAvailablePartnerReport(
    val id: Long,
    val reportNumber: Int,
    val projectReport: CorrectionProjectReport?,

    val availableFunds: List<CorrectionAvailableFund>,
)
