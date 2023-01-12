package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportControlWorkOverview(
    private val getReportControlWorkOverviewService: GetReportControlWorkOverviewService,
) : GetReportControlWorkOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlWorkOverviewException::class)
    override fun get(partnerId: Long, reportId: Long): ControlWorkOverview =
        getReportControlWorkOverviewService.get(partnerId, reportId)

}
