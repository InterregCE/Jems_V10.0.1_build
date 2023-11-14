package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetReportExpenditureCostCategoryCalculatorService(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
) {

    /**
     * This fun will either
     *  - just fetch reported expenditures for submitted report
     *  - or calculate currently reported values from actual expenditures
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdown {
        val reportStatus = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId).status
        val data = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId = reportId)

        val costCategories = data.toLinesModel()

        if (reportStatus.isOpenForNumbersChanges()) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            currentExpenditures.fillCurrencyRates(status = reportStatus, ratesResolver = { getActualCurrencyRates() })
            costCategories.fillInCurrent(current = currentExpenditures.calculateCurrent(data.options))
            costCategories.fillInCurrentReIncluded(currentReIncluded = currentExpenditures.onlyReIncluded().calculateCurrent(data.options))
        }

        return costCategories.fillInOverviewFields()
    }

    private fun getActualCurrencyRates() = with(LocalDate.now()) {
        currencyPersistence.findAllByIdYearAndIdMonth(year = year, month = monthValue)
            .associateBy { it.code }
    }

}
