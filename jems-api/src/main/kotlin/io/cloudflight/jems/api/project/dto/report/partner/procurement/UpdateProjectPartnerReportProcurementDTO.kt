package io.cloudflight.jems.api.project.dto.report.partner.procurement

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class UpdateProjectPartnerReportProcurementDTO(
    val id: Long,
    val contractId: String,
    val contractType: Set<InputTranslation>,
    val contractAmount: BigDecimal,
    val currencyCode: String,
    val supplierName: String,
    val comment: Set<InputTranslation>,
)
