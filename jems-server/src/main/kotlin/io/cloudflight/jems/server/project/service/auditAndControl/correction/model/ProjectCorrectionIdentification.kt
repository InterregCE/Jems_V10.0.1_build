package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

import java.time.ZonedDateTime

data class ProjectCorrectionIdentification(
    val correction: ProjectAuditControlCorrection,
    val followUpOfCorrectionId: Long?,
    val correctionFollowUpType: CorrectionFollowUpType,
    val repaymentFrom: ZonedDateTime?,
    val lateRepaymentTo: ZonedDateTime?,
    val partnerId: Long?,
    val partnerReportId: Long?,
    val programmeFundId: Long?
)
