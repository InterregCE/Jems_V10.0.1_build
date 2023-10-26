package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

data class CorrectionProjectReport(
    val id: Long,
    val number: Int,
    val ecPayment: CorrectionEcPayment?
)
