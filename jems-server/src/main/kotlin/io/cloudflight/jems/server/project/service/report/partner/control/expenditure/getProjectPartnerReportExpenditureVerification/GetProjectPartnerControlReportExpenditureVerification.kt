package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.common.SENSITIVE_FILE_NAME_MAKS
import io.cloudflight.jems.server.common.anonymize
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) : GetProjectPartnerControlReportExpenditureVerificationInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerControlReportExpenditureVerificationException::class)
    override fun getExpenditureVerification(
        partnerId: Long,
        reportId: Long
    ): List<ProjectPartnerReportExpenditureVerification> {
        val expenditures = reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId, reportId)

        if (!sensitiveDataAuthorization.canViewPartnerSensitiveData(partnerId)) {
            expenditures.anonymizeSensitiveData()
        }

        return expenditures
    }

    fun List<ProjectPartnerReportExpenditureVerification>.anonymizeSensitiveData() {
        this.forEach { expenditureCost ->
            expenditureCost.takeIf { it.gdpr }?.apply {
                this.comment = this.comment.anonymize()
                this.description = this.description.anonymize()
                this.attachment?.name = SENSITIVE_FILE_NAME_MAKS
            }
        }
    }
}
