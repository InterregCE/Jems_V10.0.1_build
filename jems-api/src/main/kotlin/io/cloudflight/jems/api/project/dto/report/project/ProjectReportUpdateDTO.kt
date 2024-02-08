package io.cloudflight.jems.api.project.dto.report.project

import io.cloudflight.jems.api.project.dto.contracting.reporting.ContractingDeadlineTypeDTO
import java.time.LocalDate

data class ProjectReportUpdateDTO(
    val startDate: LocalDate?,
    val endDate: LocalDate?,

    val deadlineId: Long?,
    val type: ContractingDeadlineTypeDTO?,
    val periodNumber: Int?,
    val reportingDate: LocalDate?,
    val finalReport: Boolean?,
)
