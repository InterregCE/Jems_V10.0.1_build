package io.cloudflight.jems.server.project.service.report.partner.workflow.startControlPartnerReport

import io.cloudflight.jems.server.project.service.report.model.ReportStatus

interface StartControlPartnerReportInteractor {
    fun startControl(partnerId: Long, reportId: Long): ReportStatus
}
