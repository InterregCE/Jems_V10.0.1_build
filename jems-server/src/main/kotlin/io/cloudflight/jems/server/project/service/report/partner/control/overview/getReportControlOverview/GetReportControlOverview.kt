package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportControlOverview(
   private val getReportControlOverviewService: GetReportControlOverviewService
): GetReportControlOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlOverviewException::class)
    override fun get(partnerId: Long, reportId: Long): ControlOverview  =
        getReportControlOverviewService.get(partnerId, reportId)
}
