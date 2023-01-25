package io.cloudflight.jems.server.project.service.report.project.identification

import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate

interface ProjectReportIdentificationPersistence {

    fun getReportIdentification(projectId: Long, reportId: Long): ProjectReportIdentification

    fun updateReportIdentification(
        projectId: Long,
        reportId: Long,
        identification: ProjectReportIdentificationUpdate
    ): ProjectReportIdentification
}
