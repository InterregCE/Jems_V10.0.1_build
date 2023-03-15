package io.cloudflight.jems.server.project.service.report.project.workPlan.getProjectReportWorkPlan

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportWorkPlan(
    private val reportWorkPlanPersistence: ProjectReportWorkPlanPersistence,
) : GetProjectReportWorkPlanInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportWorkPlanException::class)
    override fun get(projectId: Long, reportId: Long): List<ProjectReportWorkPackage> =
        reportWorkPlanPersistence.getReportWorkPlanById(projectId = projectId, reportId = reportId)

}
