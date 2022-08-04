package io.cloudflight.jems.api.project.dto.report.partner.procurement

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportProcurementChangeDTO(
    val id: Long? = null,
    val contractName: String,
    val referenceNumber: String,
    val contractDate: LocalDate?,
    val contractType: String,
    val contractAmount: BigDecimal,
    val currencyCode: String,
    val supplierName: String,
    val vatNumber: String,
    val comment: String,
)
