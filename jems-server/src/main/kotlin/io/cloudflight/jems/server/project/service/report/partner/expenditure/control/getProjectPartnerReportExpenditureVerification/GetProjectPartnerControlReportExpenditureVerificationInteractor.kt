package io.cloudflight.jems.server.project.service.report.partner.expenditure.control.getProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification

interface GetProjectPartnerControlReportExpenditureVerificationInteractor {
    fun getExpenditureVerification(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureVerification>
}
