package io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportProcurementSubcontractChangeDTO(
    val id: Long? = null,
    val contractName: String,
    val referenceNumber: String,
    val contractDate: LocalDate?,
    val contractAmount: BigDecimal,
    val currencyCode: String,
    val supplierName: String,
    val vatNumber: String,
)
