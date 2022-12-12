package io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

interface FinalizeControlPartnerReportInteractor {

    fun finalizeControl(partnerId: Long, reportId: Long): ReportStatus

}
