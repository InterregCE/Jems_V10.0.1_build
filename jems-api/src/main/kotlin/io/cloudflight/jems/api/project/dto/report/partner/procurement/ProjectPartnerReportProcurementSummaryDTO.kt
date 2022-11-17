package io.cloudflight.jems.api.project.dto.report.partner.procurement

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectPartnerReportProcurementSummaryDTO(
    val id: Long,
    val reportId: Long,
    val reportNumber: Int,
    val createdInThisReport: Boolean,
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
