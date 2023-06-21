package io.cloudflight.jems.server.project.service.report.partner.base.reOpenControlPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

interface ReOpenControlPartnerReportInteractor {

    fun reOpen(partnerId: Long, reportId: Long): ReportStatus
}
