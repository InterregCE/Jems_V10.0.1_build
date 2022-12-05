package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview

interface GetReportControlWorkOverviewInteractor {

    fun get(partnerId: Long, reportId: Long): ControlWorkOverview

}
