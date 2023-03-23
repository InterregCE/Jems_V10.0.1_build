package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.fillActualCurrencyRates
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetReportExpenditureInvestmentsBreakdownCalculator(
    private val expenditureInvestmentPersistence: ProjectPartnerReportInvestmentPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence
) {

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ExpenditureInvestmentBreakdown {
        val report = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId).status
        val data = expenditureInvestmentPersistence.getInvestments(partnerId = partnerId, reportId = reportId)

        if (report.isOpen()) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            currentExpenditures.fillActualCurrencyRates(getActualCurrencyRates())
            data.fillInCurrent(current = currentExpenditures.getCurrentForInvestments())
        }
        val investmentLines = data.fillInOverviewFields()

        return ExpenditureInvestmentBreakdown(
            investments = investmentLines,
            total = investmentLines.sumUp().fillInOverviewFields(),
        )
    }

    private fun getActualCurrencyRates() = with(LocalDate.now()) {
        return@with currencyPersistence.findAllByIdYearAndIdMonth(year = year, month = monthValue)
    }
}
