package io.cloudflight.jems.server.project.service.report.model.procurement

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportProcurementChange(
    val id: Long,
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
