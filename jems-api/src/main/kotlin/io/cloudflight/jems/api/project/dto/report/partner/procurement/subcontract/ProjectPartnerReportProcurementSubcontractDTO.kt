package io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportProcurementSubcontractDTO(
    val id: Long? = null,
    val reportId: Long,
    val createdInThisReport: Boolean,
    val contractName: String,
    val referenceNumber: String,
    val contractDate: LocalDate?,
    val contractAmount: BigDecimal,
    val currencyCode: String,
    val supplierName: String,
    val vatNumber: String,
)
