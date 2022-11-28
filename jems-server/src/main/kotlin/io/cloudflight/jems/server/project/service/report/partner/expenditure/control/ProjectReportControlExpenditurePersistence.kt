package io.cloudflight.jems.server.project.service.report.partner.expenditure.control

import io.cloudflight.jems.server.project.repository.report.expenditure.control.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification

interface ProjectReportControlExpenditurePersistence {
    fun getPartnerControlReportExpenditureVerification(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureVerification>

    fun updatePartnerControlReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ExpenditureVerificationUpdate>,
    ): List<ProjectPartnerReportExpenditureVerification>

}
