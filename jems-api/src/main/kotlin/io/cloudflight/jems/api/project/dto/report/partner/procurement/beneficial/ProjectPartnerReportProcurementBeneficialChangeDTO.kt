package io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial

import java.time.LocalDate

data class ProjectPartnerReportProcurementBeneficialChangeDTO(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
)
