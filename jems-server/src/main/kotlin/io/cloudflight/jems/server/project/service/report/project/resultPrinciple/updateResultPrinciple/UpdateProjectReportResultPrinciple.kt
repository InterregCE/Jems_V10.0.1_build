package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportResultPrinciple(
    private val projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
) : UpdateProjectReportResultPrincipleInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportResultPrincipleException::class)
    override fun update(projectId: Long, reportId: Long, resultPrinciple: ProjectReportResultPrincipleUpdate): ProjectReportResultPrinciple {
        validateReportNotSubmitted(projectReportPersistence.getReportById(projectId = projectId, reportId = reportId).status)

        return projectReportResultPrinciplePersistence.updateProjectReportResultPrinciple(
            projectId = projectId,
            reportId = reportId,
            newResultsAndPrinciples = resultPrinciple
        )
    }

    private fun validateReportNotSubmitted(status: ProjectReportStatus) {
        if (status.isClosed())
            throw ProjectReportClosedException()
    }
}
