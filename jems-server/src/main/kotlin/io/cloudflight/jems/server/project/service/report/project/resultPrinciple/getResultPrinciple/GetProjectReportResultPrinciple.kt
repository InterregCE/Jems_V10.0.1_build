package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.getResultPrinciple

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportResultPrinciple(
    private val projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
): GetProjectReportResultPrincipleInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportResultPrincipleException::class)
    override fun get(projectId: Long, reportId: Long): ProjectReportResultPrinciple =
        projectReportResultPrinciplePersistence.getProjectResultPrinciples(projectId = projectId, reportId = reportId)
}
