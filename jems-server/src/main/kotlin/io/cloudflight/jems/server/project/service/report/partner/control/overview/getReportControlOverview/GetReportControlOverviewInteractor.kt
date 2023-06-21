package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview

import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview

interface GetReportControlOverviewInteractor {

    fun get(partnerId: Long, reportId: Long): ControlOverview
}
