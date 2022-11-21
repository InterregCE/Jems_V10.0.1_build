package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification

interface GetProjectPartnerControlReportExpenditureVerificationInteractor {
    fun getExpenditureVerification(partnerId: Long, reportId: Long): List<ProjectPartnerControlReportExpenditureVerification>
}
