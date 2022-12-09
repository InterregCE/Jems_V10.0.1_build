package io.cloudflight.jems.server.project.controller.report.partner.control.overview

import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerReportControlOverviewApi
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.GetReportControlWorkOverviewInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportControlOverviewController(
    private val getReportControlWorkOverview: GetReportControlWorkOverviewInteractor,
) : ProjectPartnerReportControlOverviewApi {

    override fun getControlWorkOverview(partnerId: Long, reportId: Long) =
        getReportControlWorkOverview.get(partnerId = partnerId, reportId = reportId).toDto()

}
