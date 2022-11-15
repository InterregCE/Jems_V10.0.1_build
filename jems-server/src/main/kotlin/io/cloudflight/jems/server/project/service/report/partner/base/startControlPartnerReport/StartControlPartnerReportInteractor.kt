package io.cloudflight.jems.server.project.service.report.partner.base.startControlPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

interface StartControlPartnerReportInteractor {
    fun startControl(partnerId: Long, reportId: Long): ReportStatus
}
