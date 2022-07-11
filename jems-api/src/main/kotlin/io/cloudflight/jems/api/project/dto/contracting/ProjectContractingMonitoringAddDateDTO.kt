package io.cloudflight.jems.api.project.dto.contracting

import java.time.LocalDate

data class ProjectContractingMonitoringAddDateDTO(
    val projectId: Long,
    val number: Long,

    val entryIntoForceDate: LocalDate? = null,
    val comment: String? = null

)
