package io.cloudflight.jems.server.project.service.auditAndControl.model.correction

import java.time.LocalDate

data class AuditControlCorrectionUpdate(
    val followUpOfCorrectionId: Long?,
    val correctionFollowUpType: CorrectionFollowUpType,
    val repaymentFrom: LocalDate?,
    val lateRepaymentTo: LocalDate?,

    val partnerReportId: Long,
    val programmeFundId: Long,
)
