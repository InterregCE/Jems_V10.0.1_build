package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import java.time.LocalDate

data class ProjectAuditControlCorrectionDTO(
    val id: Long,
    val orderNr: Int,
    val status: AuditStatusDTO,
    val type: AuditControlCorrectionTypeDTO,

    val auditControlId: Long,
    val auditControlNumber: Int,

    val followUpOfCorrectionId: Long?,
    val correctionFollowUpType: CorrectionFollowUpTypeDTO?,
    val repaymentFrom: LocalDate?,
    val lateRepaymentTo: LocalDate?,
    val partnerId: Long?,
    val partnerReportId: Long?,
    val programmeFundId: Long?,

    val costCategory: CorrectionCostCategoryDTO?,
    val expenditureCostItem: CorrectionCostItemDTO?,
    val procurementId: Long?,

)
