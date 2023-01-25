package io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification

import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate

interface UpdateProjectReportIdentificationInteractor {

    fun updateIdentification(
        projectId: Long,
        reportId: Long,
        identification: ProjectReportIdentificationUpdate,
    ): ProjectReportIdentification

}
