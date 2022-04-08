package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GetProjectPartnerReportExpenditure(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
) : GetProjectPartnerReportExpenditureInteractor {

    @CanViewPartnerReport
    @ExceptionWrapper(GetProjectPartnerReportExpenditureException::class)
    override fun getExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> {
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
