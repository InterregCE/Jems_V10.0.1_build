package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportIdentification(
    private val projectReportIdentification: ProjectReportIdentificationPersistence
): GetProjectReportIdentificationInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportIdentificationException::class)
    override fun getIdentification(projectId: Long, reportId: Long): ProjectReportIdentification {
        return projectReportIdentification.getReportIdentification(projectId, reportId)
    }
}
