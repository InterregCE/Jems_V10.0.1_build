package io.cloudflight.jems.server.project.service.report.model.project.base

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import java.time.LocalDate

data class ProjectReportDeadline(
    val deadlineId: Long?,
    val type: ContractingDeadlineType?,
    val periodNumber: Int?,
    val reportingDate: LocalDate?,
    val finalReport: Boolean?,
)
