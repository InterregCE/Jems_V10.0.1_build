package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import java.util.SortedSet
import java.util.TreeSet

fun ProjectContractingReportingEntity.toModel(linkedReports: List<ProjectReportEntity>) = ProjectContractingReportingSchedule(
    id = id,
    type = type,
    periodNumber = periodNumber,
    date = deadline,
    comment = comment,
    number = number,
    linkedSubmittedProjectReportNumbers = linkedReports.getProjectReportsThat { it.status.hasBeenSubmitted() },
    linkedDraftProjectReportNumbers = linkedReports.getProjectReportsThat { it.status.isOpenInitially() },
    finalReport = finalReport
)

private inline fun Collection<ProjectReportEntity>.getProjectReportsThat(
    selectorFunction: (ProjectReportEntity) -> Boolean,
): SortedSet<Int> = filter { selectorFunction.invoke(it) }.mapTo(TreeSet()) { it.number }
