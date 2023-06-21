package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.anonymizeSensitiveDataIf
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetProjectPartnerReportExpenditureCalculator(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) {

    @Transactional(readOnly = true)
    fun getExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> {
        val isOpen = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId)
            .status.isOpenForNumbersChanges()
        val expenditures =
            reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)

        expenditures.anonymizeSensitiveDataIf(
            canNotWorkWithSensitive = !sensitiveDataAuthorization.canViewPartnerSensitiveData(partnerId))

        return if (isOpen)
            expenditures.apply {
                val today = LocalDate.now()
                val rates = currencyPersistence.findAllByIdYearAndIdMonth(year = today.year, month = today.monthValue)
                    .associateBy { it.code }
                this.fillCurrencyRates(rates)
            }
        else
            expenditures
    }
}
