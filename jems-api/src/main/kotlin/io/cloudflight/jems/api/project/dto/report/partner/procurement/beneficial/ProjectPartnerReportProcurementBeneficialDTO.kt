package io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial

import java.time.LocalDate

data class ProjectPartnerReportProcurementBeneficialDTO(
    val id: Long? = null,
    val reportId: Long,
    val createdInThisReport: Boolean,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
)
