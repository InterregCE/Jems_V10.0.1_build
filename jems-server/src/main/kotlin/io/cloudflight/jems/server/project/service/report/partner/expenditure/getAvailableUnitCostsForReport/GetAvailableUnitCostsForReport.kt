package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableUnitCostsForReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetAvailableUnitCostsForReport (
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
) : GetAvailableUnitCostsForReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableUnitCostsForReportException::class)
    override fun getUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCost> =
        reportExpenditurePersistence.getAvailableUnitCosts(partnerId = partnerId, reportId = reportId)
            .filter { it.total.compareTo(BigDecimal.ZERO) != 0 }

}
