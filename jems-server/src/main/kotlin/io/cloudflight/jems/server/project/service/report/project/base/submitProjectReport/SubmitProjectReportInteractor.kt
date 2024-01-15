package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

interface SubmitProjectReportInteractor {

    fun submit(reportId: Long): ProjectReportStatus

}
