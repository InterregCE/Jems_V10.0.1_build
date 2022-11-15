package io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary

interface CreateProjectPartnerReportInteractor {
    fun createReportFor(partnerId: Long): ProjectPartnerReportSummary
}
