package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.fillActualCurrencyRates
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetReportExpenditureInvestmentsBreakdown(
    private val expenditureInvestmentPersistence: ProjectReportExpenditureInvestmentPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val reportPersistence: ProjectReportPersistence
) : GetReportExpenditureInvestmentsBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureInvestmentsBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long): ExpenditureInvestmentBreakdown {

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId)

        val data = expenditureInvestmentPersistence.getInvestments(partnerId = partnerId, reportId = reportId)

        if (!report.status.isClosed()) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            currentExpenditures.fillActualCurrencyRates(getActualCurrencyRates())
            data.fillInCurrent(current = currentExpenditures.getCurrentForInvestments())
        }
        val investmentLines = data.fillInOverviewFields()

        return ExpenditureInvestmentBreakdown(
            investments = investmentLines,
            total = investmentLines.sumUp(),
        )
    }

    private fun getActualCurrencyRates() = with(LocalDate.now()) {
        return@with currencyPersistence.findAllByIdYearAndIdMonth(year = year, month = monthValue)
    }
}
