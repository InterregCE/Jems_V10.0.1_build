package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import java.time.ZonedDateTime

data class ProjectCorrectionIdentificationUpdateDTO(
    val followUpOfCorrectionId: Long?,
    val correctionFollowUpType: CorrectionFollowUpTypeDTO,
    val repaymentFrom: ZonedDateTime?,
    val lateRepaymentTo: ZonedDateTime?,

    val partnerId: Long?,
    val partnerReportId: Long?,
    val programmeFundId: Long?
)
