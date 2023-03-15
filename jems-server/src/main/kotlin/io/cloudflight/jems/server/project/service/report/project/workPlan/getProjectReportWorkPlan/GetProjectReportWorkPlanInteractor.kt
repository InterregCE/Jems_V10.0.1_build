package io.cloudflight.jems.server.project.service.report.project.workPlan.getProjectReportWorkPlan

import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage

interface GetProjectReportWorkPlanInteractor {

    fun get(projectId: Long, reportId: Long): List<ProjectReportWorkPackage>

}
