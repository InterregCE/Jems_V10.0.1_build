package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportParkedExpenditureIds

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification.GetProjectPartnerControlReportParkedExpenditureIdsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerControlReportParkedExpenditures(
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence
) : GetProjectPartnerControlReportParkedExpendituresInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerControlReportParkedExpenditureIdsException::class)
    override fun getParkedExpenditureIds(partnerId: Long, reportId: Long): List<Long> =
        reportParkedExpenditurePersistence.getParkedExpenditureIds(reportId).toList()
}
