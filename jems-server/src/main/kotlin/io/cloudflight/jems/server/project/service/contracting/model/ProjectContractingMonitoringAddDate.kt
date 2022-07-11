package io.cloudflight.jems.server.project.service.contracting.model

import java.time.LocalDate

data class ProjectContractingMonitoringAddDate(
    val projectId: Long,
    val number: Int,

    val entryIntoForceDate: LocalDate? = null,
    val comment: String? = null

)
