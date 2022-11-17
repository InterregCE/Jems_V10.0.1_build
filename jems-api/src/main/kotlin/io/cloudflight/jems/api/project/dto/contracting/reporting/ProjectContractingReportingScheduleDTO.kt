package io.cloudflight.jems.api.project.dto.contracting.reporting

import java.time.LocalDate

data class ProjectContractingReportingScheduleDTO(
    val id: Long? = null,
    val type: ContractingDeadlineTypeDTO,
    val periodNumber: Int?,
    val date: LocalDate?,
    val comment: String,
)
