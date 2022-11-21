package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate

interface UpdateProjectPartnerControlReportExpenditureVerificationInteractor {
    fun updatePartnerReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdate>,
    ): List<ProjectPartnerControlReportExpenditureVerification>
}
