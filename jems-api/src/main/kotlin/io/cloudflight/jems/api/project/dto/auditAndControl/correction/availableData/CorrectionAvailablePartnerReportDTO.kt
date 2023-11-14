package io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class CorrectionAvailablePartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val projectReport: CorrectionProjectReportDTO?,

    val availableReportFunds: List<ProgrammeFundDTO>,

    val availablePayments: List<CorrectionAvailablePaymentDTO>,
)
