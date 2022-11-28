package io.cloudflight.jems.server.project.service.report.partner.expenditure.control.getProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.expenditure.control.ProjectReportControlExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectReportControlExpenditurePersistence
) : GetProjectPartnerControlReportExpenditureVerificationInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerControlReportExpenditureVerificationException::class)
    override fun getExpenditureVerification(
        partnerId: Long,
        reportId: Long
    ): List<ProjectPartnerReportExpenditureVerification> =
        reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId, reportId)
}
