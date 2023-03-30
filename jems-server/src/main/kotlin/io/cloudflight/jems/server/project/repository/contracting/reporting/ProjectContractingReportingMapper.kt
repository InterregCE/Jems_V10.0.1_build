package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

fun ProjectContractingReportingEntity.toModel(reportsByDeadline: List<ProjectReportEntity>?) = ProjectContractingReportingSchedule(
    id = id,
    type = type,
    periodNumber = periodNumber,
    date = deadline,
    comment = comment,
    number = number,
    linkedSubmittedProjectReportNumbers = reportsByDeadline?.filter {
        ProjectReportStatus.SUBMITTED_STATUSES.contains(it.status) }?.map { it.number }?.toSet() ?: setOf(),
    linkedDraftProjectReportNumbers = reportsByDeadline?.filter {
        it.status == ProjectReportStatus.Draft }?.map { it.number }?.toSet() ?: setOf(),
)
