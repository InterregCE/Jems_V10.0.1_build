package io.cloudflight.jems.server.project.service.report.model.procurement.subcontract

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportProcurementSubcontract(
    val id: Long,
    val reportId: Long,
    var createdInThisReport: Boolean = false,
    val contractName: String,
    val referenceNumber: String,
    val contractDate: LocalDate?,
    val contractAmount: BigDecimal,
    val currencyCode: String,
    val supplierName: String,
    val vatNumber: String,
)
