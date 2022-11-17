package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary

interface ProjectReportCreatePersistence {

    fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary

}
