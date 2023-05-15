package io.cloudflight.jems.server.project.controller.report.partner.control.overview

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlOverviewDTO
import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerControlReportOverviewApi
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview.GetReportControlDeductionOverviewInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview.GetReportControlOverviewInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.GetReportControlWorkOverviewInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.updateReportControlOverview.UpdateReportControlOverviewInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportControlOverviewController(
    private val getReportControlWorkOverview: GetReportControlWorkOverviewInteractor,
    private val getReportControlOverview: GetReportControlOverviewInteractor,
    private val updateReportControlOverview: UpdateReportControlOverviewInteractor,
    private val getReportControlDeductionOverview: GetReportControlDeductionOverviewInteractor
) : ProjectPartnerControlReportOverviewApi {

    override fun getControlWorkOverview(partnerId: Long, reportId: Long) =
        getReportControlWorkOverview.get(partnerId = partnerId, reportId = reportId).toDto()

    override fun getControlDeductionByTypologyOfErrorsOverview(
        partnerId: Long,
        reportId: Long,
        linkedFormVersion: String?
    ): ControlDeductionOverviewDTO =
        getReportControlDeductionOverview.get(partnerId, reportId).toDto()


    override fun getControlOverview(partnerId: Long, reportId: Long) =
        getReportControlOverview.get(partnerId, reportId).toDto()

    override fun updateControlOverview(
        partnerId: Long,
        reportId: Long,
        controlOverview: ControlOverviewDTO
    ): ControlOverviewDTO =
        updateReportControlOverview.update(partnerId, reportId, controlOverview.toModel()).toDto()
}
