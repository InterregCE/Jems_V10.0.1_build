package io.cloudflight.jems.server.project.service.report.partner.expenditure.control.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate

interface UpdateProjectPartnerControlReportExpenditureVerificationInteractor {
    fun updatePartnerReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerReportExpenditureVerificationUpdate>,
    ): List<ProjectPartnerReportExpenditureVerification>
}
