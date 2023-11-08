package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class CorrectionAvailablePartnerReport(
    val id: Long,
    val reportNumber: Int,
    val projectReport: CorrectionProjectReport?,

    val availableReportFunds: List<ProgrammeFund>,

    val availablePayments: List<CorrectionAvailablePayment>
)
