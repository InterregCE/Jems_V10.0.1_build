package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary

interface CreateProjectPartnerReportInteractor {
    fun createReportFor(partnerId: Long): ProjectPartnerReportSummary
}
