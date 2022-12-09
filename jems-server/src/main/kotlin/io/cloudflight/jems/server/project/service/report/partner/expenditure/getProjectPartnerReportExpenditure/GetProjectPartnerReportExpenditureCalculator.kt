package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetProjectPartnerReportExpenditureCalculator(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
) {

    @Transactional(readOnly = true)
    fun getExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> {
        val isSubmitted = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId).status.isClosed()
        val expenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)

        return if (isSubmitted)
            expenditures
        else
            expenditures.apply {
                val today = LocalDate.now()
                val rates = currencyPersistence.findAllByIdYearAndIdMonth(year = today.year, month = today.monthValue).associateBy { it.code }
                this.fillCurrencyRates(rates)
            }
    }

}
