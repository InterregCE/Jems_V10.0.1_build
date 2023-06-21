package io.cloudflight.jems.server.project.service.report.project.base

import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportCreateModel

interface ProjectReportCreatePersistence {

    fun createReportAndFillItToEmptyCertificates(reportToCreate: ProjectReportCreateModel): ProjectReportModel

}
