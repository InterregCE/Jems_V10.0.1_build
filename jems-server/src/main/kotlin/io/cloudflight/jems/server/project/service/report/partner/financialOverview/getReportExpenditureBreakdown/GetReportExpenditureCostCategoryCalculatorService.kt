package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetReportExpenditureCostCategoryCalculatorService(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
) {

    /**
     * This fun will either
     *  - just fetch reported expenditures for submitted report
     *  - or calculate currently reported values from actual expenditures
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdown {
        val isSubmitted = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId).status.isClosed()
        val data = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId = reportId)

        val costCategories = data.toLinesModel()

        if (!isSubmitted) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            currentExpenditures.fillCurrentCurrencies()
            costCategories.fillInCurrent(current = currentExpenditures.calculateCurrent(data.options))
        }

        return costCategories.fillInOverviewFields()
    }

    private fun List<ProjectPartnerReportExpenditureCost>.fillCurrentCurrencies() = apply {
        val today = LocalDate.now()
        val rates = currencyPersistence.findAllByIdYearAndIdMonth(year = today.year, month = today.monthValue).associateBy { it.code }
        this.fillCurrencyRates(rates)
    }

}
