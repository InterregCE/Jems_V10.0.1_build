package io.cloudflight.jems.server.project.service.report.partner.control.overview.updateReportControlOverview

import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview

interface UpdateReportControlOverviewInteractor {

    fun update(partnerId: Long, reportId: Long, controlOverview: ControlOverview): ControlOverview
}
