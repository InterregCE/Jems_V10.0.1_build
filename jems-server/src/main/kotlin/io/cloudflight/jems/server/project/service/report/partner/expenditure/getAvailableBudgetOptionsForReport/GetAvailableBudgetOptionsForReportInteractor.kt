package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableBudgetOptionsForReport

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions

interface GetAvailableBudgetOptionsForReportInteractor {
    fun getBudgetOptions(partnerId: Long, reportId: Long): ProjectPartnerBudgetOptions
}
