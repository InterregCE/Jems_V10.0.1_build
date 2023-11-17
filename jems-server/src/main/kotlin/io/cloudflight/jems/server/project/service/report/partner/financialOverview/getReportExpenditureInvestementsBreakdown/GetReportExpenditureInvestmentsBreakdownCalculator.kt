package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
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
        val reportStatus = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId).status
        val data = expenditureInvestmentPersistence.getInvestments(partnerId = partnerId, reportId = reportId)

        if (reportStatus.isOpenForNumbersChanges()) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            currentExpenditures.fillCurrencyRates(status = reportStatus, ratesResolver = {getActualCurrencyRates() })
            data.fillInCurrent(current = currentExpenditures.getCurrentForInvestments())
        }
        val investmentLines = data.fillInOverviewFields()

        return ExpenditureInvestmentBreakdown(
            investments = investmentLines,
            total = investmentLines.sumUp().fillInOverviewFields(),
        )
    }

    private fun getActualCurrencyRates() = with(LocalDate.now()) {
        currencyPersistence.findAllByIdYearAndIdMonth(year = year, month = monthValue)
            .associateBy { it.code }
    }
}
