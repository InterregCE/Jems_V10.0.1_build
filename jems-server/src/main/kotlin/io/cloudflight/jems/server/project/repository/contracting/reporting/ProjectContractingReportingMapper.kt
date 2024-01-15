package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

fun ProjectContractingReportingEntity.toModel(reports: List<ProjectReportEntity>) = ProjectContractingReportingSchedule(
    id = id,
    type = type,
    periodNumber = periodNumber,
    date = deadline,
    comment = comment,
    number = number,
    linkedSubmittedProjectReportNumbers = reports
        .filter { ProjectReportStatus.FINANCIALLY_CLOSED_STATUSES.contains(it.status) }
        .getNumbers(),
    linkedDraftProjectReportNumbers = reports
        .filter { it.status.isOpenInitially() }
        .getNumbers(),
)

private fun Collection<ProjectReportEntity>.getNumbers() = mapTo(HashSet()) { it.number }
