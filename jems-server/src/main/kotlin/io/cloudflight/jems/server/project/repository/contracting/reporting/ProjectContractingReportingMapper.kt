package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule

fun List<ProjectContractingReportingEntity>.toModel() =  map {
    ProjectContractingReportingSchedule(
        id = it.id,
        type = it.type,
        periodNumber = it.periodNumber,
        date = it.deadline,
        comment = it.comment,
    )
}
