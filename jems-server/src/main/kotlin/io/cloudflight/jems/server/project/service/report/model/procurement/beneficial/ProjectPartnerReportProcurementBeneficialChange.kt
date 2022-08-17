package io.cloudflight.jems.server.project.service.report.model.procurement.beneficial

import java.time.LocalDate

data class ProjectPartnerReportProcurementBeneficialChange(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
)
