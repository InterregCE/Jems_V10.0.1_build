package io.cloudflight.jems.server.project.service.report.project.closure.getProjectClosure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportProjectClosure(
    private val projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence
): GetProjectReportProjectClosureInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportProjectClosureException::class)
    override fun get(projectId: Long, reportId: Long): ProjectReportProjectClosure =
        projectReportProjectClosurePersistence.getProjectReportProjectClosure(reportId)

}
