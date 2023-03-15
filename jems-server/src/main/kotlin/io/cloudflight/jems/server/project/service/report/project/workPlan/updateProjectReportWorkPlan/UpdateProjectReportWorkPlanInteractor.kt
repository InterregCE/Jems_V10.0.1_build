package io.cloudflight.jems.server.project.service.report.project.workPlan.updateProjectReportWorkPlan

import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageUpdate

interface UpdateProjectReportWorkPlanInteractor {

    fun update(
        projectId: Long,
        reportId: Long,
        workPlan: List<ProjectReportWorkPackageUpdate>,
    ): List<ProjectReportWorkPackage>

}
