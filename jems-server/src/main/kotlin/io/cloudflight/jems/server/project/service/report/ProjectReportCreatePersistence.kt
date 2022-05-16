package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary

interface ProjectReportCreatePersistence {

    fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary

}
