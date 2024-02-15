package io.cloudflight.jems.server.project.service.report.model.project

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import java.time.LocalDate

data class ProjectReportUpdate(
    val startDate: LocalDate?,
    val endDate: LocalDate?,

    val deadlineId: Long?,
    val type: ContractingDeadlineType?,
    val periodNumber: Int?,
    val reportingDate: LocalDate?,
    val finalReport: Boolean?,
)
