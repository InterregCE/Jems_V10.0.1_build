package io.cloudflight.jems.server.project.service.report.model.procurement

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportProcurementUpdate(
    val id: Long,
    val contractId: String,
    val contractType: Set<InputTranslation>,
    val contractAmount: BigDecimal,
    val supplierName: String,
    val comment: Set<InputTranslation>,
)
