package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectControlReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectControlReportExpenditurePersistence
) : GetProjectPartnerControlReportExpenditureVerificationInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerControlReportExpenditureVerificationException::class)
    override fun getExpenditureVerification(
        partnerId: Long,
        reportId: Long
    ): List<ProjectPartnerControlReportExpenditureVerification> =
        reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId, reportId)
}
