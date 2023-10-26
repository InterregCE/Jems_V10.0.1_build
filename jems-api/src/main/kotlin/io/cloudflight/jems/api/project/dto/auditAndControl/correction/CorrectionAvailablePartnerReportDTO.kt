package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class CorrectionAvailablePartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val projectReport: CorrectionProjectReportDTO?,

    val availableFunds: List<ProgrammeFundDTO>
)
