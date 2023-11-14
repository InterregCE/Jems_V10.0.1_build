package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetReportExpenditureUnitCostBreakdownCalculator(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
) {

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ExpenditureUnitCostBreakdown {
        val reportStatus = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId).status

        val data = reportUnitCostPersistence.getUnitCost(partnerId = partnerId, reportId = reportId)

        if (reportStatus.isOpenForNumbersChanges()) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            currentExpenditures.fillCurrencyRates(status = reportStatus, ratesResolver = { getActualCurrencyRates() })
            data.fillInCurrent(current = currentExpenditures.getCurrentForUnitCosts())
        }
        val unitCostLines = data.fillInOverviewFields()

        return ExpenditureUnitCostBreakdown(
            unitCosts = unitCostLines,
            total = unitCostLines.sumUp().fillInOverviewFields(),
        )
    }

    private fun getActualCurrencyRates() = with(LocalDate.now()) {
        currencyPersistence.findAllByIdYearAndIdMonth(year = year, month = monthValue)
            .associateBy { it.code }
    }

}
