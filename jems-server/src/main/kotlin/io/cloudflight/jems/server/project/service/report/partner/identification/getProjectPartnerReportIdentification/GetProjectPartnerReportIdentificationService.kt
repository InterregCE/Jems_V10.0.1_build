package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectPartnerReportIdentificationService(
    private val identificationPersistence: ProjectPartnerReportIdentificationPersistence,
    private val reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService,
) {

    companion object {
        fun emptyIdentification() = ProjectPartnerReportIdentification(
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
            ),
            controllerFormats = emptySet(),
            type = ReportType.PartnerReport,
        )
    }

    @Transactional(readOnly = true)
    fun getIdentification(partnerId: Long, reportId: Long): ProjectPartnerReportIdentification {
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
