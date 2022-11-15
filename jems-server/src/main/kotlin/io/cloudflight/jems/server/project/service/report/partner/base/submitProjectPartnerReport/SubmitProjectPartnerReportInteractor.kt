package io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

interface SubmitProjectPartnerReportInteractor {
    fun submit(partnerId: Long, reportId: Long): ReportStatus
}
