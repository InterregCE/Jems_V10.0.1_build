package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class CorrectionAvailableReportTmp (
    val partnerId: Long,

    val id: Long,
    val reportNumber: Int,

    val projectReportId: Long?,
    val projectReportNumber: Int?,

    val ecPaymentId: Long?,
    val ecPaymentAccountingYearId: Long? = null,

    val availableFunds: List<ProgrammeFund>,
)
