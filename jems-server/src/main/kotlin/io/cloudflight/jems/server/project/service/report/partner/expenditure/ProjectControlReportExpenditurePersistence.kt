package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate

interface ProjectControlReportExpenditurePersistence {
    fun getPartnerControlReportExpenditureVerification(partnerId: Long, reportId: Long): List<ProjectPartnerControlReportExpenditureVerification>

    fun updatePartnerControlReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdate>,
    ): List<ProjectPartnerControlReportExpenditureVerification>
}
