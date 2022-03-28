package io.cloudflight.jems.api.project.dto.report.partner.procurement

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportProcurementDTO(
    val id: Long,
    val reportId: Long,
    val reportNumber: Int,
    val createdInThisReport: Boolean,
    val contractId: String,
    val contractType: Set<InputTranslation>,
    val contractAmount: BigDecimal,
    val supplierName: String,
    val comment: Set<InputTranslation>,
)
