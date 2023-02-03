package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview

interface GetReportControlDeductionOverviewInteractor {

    fun get(partnerId: Long, reportId: Long, linkedFormVersion: String?): ControlDeductionOverview

}
