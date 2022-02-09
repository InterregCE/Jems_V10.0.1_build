package io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary

interface SubmitProjectPartnerReportInteractor {
    fun submit(partnerId: Long, reportId: Long): ProjectPartnerReportSummary
}
