package io.cloudflight.jems.server.project.service.report.partner.control.expenditure

import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCurrencyRateChange
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification

interface ProjectPartnerReportExpenditureVerificationPersistence {

    fun getPartnerControlReportExpenditureVerification(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureVerification>

    fun getParkedExpenditureIds(partnerId: Long, reportId: Long) : List<Long>

    fun updatePartnerControlReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ExpenditureVerificationUpdate>,
    ): List<ProjectPartnerReportExpenditureVerification>

    fun updateExpenditureCurrencyRatesAndClearVerification(
        partnerId: Long,
        reportId: Long,
        newRates: Collection<ProjectPartnerReportExpenditureCurrencyRateChange>,
    ): List<ProjectPartnerReportExpenditureCost>

}
