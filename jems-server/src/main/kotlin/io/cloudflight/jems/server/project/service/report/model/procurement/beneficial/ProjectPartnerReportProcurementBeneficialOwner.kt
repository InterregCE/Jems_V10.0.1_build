package io.cloudflight.jems.server.project.service.report.model.procurement.beneficial

import java.time.LocalDate

data class ProjectPartnerReportProcurementBeneficialOwner(
    val id: Long,
    val reportId: Long,
    var createdInThisReport: Boolean = false,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
)
