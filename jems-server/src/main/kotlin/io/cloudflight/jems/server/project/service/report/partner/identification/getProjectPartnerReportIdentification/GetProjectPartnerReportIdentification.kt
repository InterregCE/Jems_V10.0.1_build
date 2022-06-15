package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectPartnerReportIdentification(
    private val identificationPersistence: ProjectReportIdentificationPersistence,
    private val reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService,
) : GetProjectPartnerReportIdentificationInteractor {

    companion object {
        private fun emptyIdentification() = ProjectPartnerReportIdentification(
            startDate = null,
            endDate = null,
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            spendingDeviations = emptySet(),
            targetGroups = emptyList(),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = null,
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ZERO,
            )
        )
    }

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportIdentificationException::class)
    override fun getIdentification(partnerId: Long, reportId: Long): ProjectPartnerReportIdentification {
        val identification = identificationPersistence
            .getPartnerReportIdentification(partnerId = partnerId, reportId = reportId)
            .orElse(emptyIdentification())

        val expendituresCalculated = reportExpenditureCostCategoryCalculatorService
            .getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId).total

        return identification.fillInCurrentAndPreviousReporting(
            currentReport = expendituresCalculated.currentReport,
            previouslyReported = expendituresCalculated.previouslyReported,
        )
    }
}
