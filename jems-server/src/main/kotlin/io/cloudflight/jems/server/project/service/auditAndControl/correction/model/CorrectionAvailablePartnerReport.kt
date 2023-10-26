package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class CorrectionAvailablePartnerReport(
    val id: Long,
    val reportNumber: Int,
    val projectReport: CorrectionProjectReport?,

    val availableFunds: List<ProgrammeFund>
)
