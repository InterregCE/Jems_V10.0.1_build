package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification

interface GetProjectReportIdentificationInteractor {

    fun getIdentification(projectId: Long, reportId: Long): ProjectReportIdentification
}
