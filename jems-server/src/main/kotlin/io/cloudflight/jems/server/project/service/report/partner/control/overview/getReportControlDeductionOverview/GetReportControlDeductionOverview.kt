package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportControlDeductionOverview(
    private val getReportControlDeductionOverviewCalculator: GetReportControlDeductionOverviewCalculator,
) : GetReportControlDeductionOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlDeductionOverviewException::class)
    override fun get(partnerId: Long, reportId: Long): ControlDeductionOverview =
        getReportControlDeductionOverviewCalculator.get(partnerId, reportId)

}
