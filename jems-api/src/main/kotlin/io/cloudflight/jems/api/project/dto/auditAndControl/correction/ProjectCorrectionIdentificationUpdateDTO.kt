package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import java.time.LocalDate

data class ProjectCorrectionIdentificationUpdateDTO(
    val followUpOfCorrectionId: Long?,
    val correctionFollowUpType: CorrectionFollowUpTypeDTO,
    val repaymentFrom: LocalDate?,
    val lateRepaymentTo: LocalDate?,

    val partnerReportId: Long?,
    val partnerId: Long?,
    val lumpSumOrderNr: Int?,
    val programmeFundId: Long,

    val costCategory: CorrectionCostCategoryDTO?,
    val procurementId: Long?,
    val expenditureId: Long?,
)
