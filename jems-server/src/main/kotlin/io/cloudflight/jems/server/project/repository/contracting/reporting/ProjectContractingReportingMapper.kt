package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule

fun List<ProjectContractingReportingEntity>.toModel() =  map { it.toModel() }

fun ProjectContractingReportingEntity.toModel() = ProjectContractingReportingSchedule(
    id = id,
    type = type,
    periodNumber = periodNumber,
    date = deadline,
    comment = comment,
)
