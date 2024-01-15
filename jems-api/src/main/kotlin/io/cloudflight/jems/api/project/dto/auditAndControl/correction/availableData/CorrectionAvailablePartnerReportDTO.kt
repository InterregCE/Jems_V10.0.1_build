package io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData

data class CorrectionAvailablePartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val projectReport: CorrectionProjectReportDTO?,

    val availableFunds: List<CorrectionAvailableFundDTO>,
)
