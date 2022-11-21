package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectControlReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectControlReportExpenditurePersistence
) : UpdateProjectPartnerControlReportExpenditureVerificationInteractor {

    @CanEditPartnerControlReport
    @Transactional()
    @ExceptionWrapper(UpdateProjectPartnerControlReportExpenditureVerificationException::class)
    override fun updatePartnerReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdate>
    ): List<ProjectPartnerControlReportExpenditureVerification> {
        if (expenditureVerification.any { it.deductedAmount !== BigDecimal.ZERO && it.typologyOfErrorId == null })
            throw TypologyOfErrorsIdIsNullException()

        return reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId, reportId, expenditureVerification)
    }
}
