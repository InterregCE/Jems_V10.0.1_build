package io.cloudflight.jems.server.project.service.contracting.model.reporting

import java.time.LocalDate

data class ProjectContractingReportingSchedule(
    val id: Long,
    val type: ContractingDeadlineType,
    val periodNumber: Int,
    val date: LocalDate,
    val comment: String,
)
