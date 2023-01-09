package io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReIncludeParkedExpenditure(
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
) : ReIncludeParkedExpenditureInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(ReIncludeParkedExpenditureException::class)
    override fun reIncludeParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long) {
        reportExpenditurePersistence.reIncludeParkedExpenditure(partnerId, reportId = reportId, expenditureId = expenditureId)
        reportParkedExpenditurePersistence.unParkExpenditures(setOf(expenditureId))
    }

}
