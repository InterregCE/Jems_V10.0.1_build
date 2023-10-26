package io.cloudflight.jems.api.project.dto.auditAndControl.correction

data class CorrectionProjectReportDTO(
    val id: Long,
    val number: Int,
    val ecPayment: CorrectionEcPaymentDTO?
)
