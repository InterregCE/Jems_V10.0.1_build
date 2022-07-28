package io.cloudflight.jems.server.project.service.report.model.procurement

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectPartnerReportProcurementSummary(
    val id: Long,
    val reportId: Long,
    val reportNumber: Int,
    var createdInThisReport: Boolean = false,
    val lastChanged: ZonedDateTime,
    val contractName: String,
    val referenceNumber: String,
    val contractDate: LocalDate?,
    val contractType: String,
    val contractAmount: BigDecimal,
    val currencyCode: String,
    val supplierName: String,
    val vatNumber: String,
)
