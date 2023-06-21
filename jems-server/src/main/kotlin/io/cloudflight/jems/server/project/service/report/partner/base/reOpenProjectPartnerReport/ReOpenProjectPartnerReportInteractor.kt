package io.cloudflight.jems.server.project.service.report.partner.base.reOpenProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

interface ReOpenProjectPartnerReportInteractor {
    fun reOpen(partnerId: Long, reportId: Long): ReportStatus
}
