package io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods.filterOutPreparationAndClosure
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.fillInCurrentAndPreviousReporting
import java.math.BigDecimal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerReportIdentification(
    private val reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence,
    private val reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportIdentificationInteractor {

    companion object {
        private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportIdentificationException::class)
    override fun updateIdentification(
        partnerId: Long,
        reportId: Long,
        data: UpdateProjectPartnerReportIdentification
    ): ProjectPartnerReportIdentification {
        validateInputs(data = data)
        validatePeriod(
            period = data.period,
            availablePeriods = reportIdentificationPersistence.getAvailablePeriods(partnerId, reportId = reportId)
                .filterOutPreparationAndClosure(),
        )

        val identification = reportIdentificationPersistence.updatePartnerReportIdentification(
            partnerId = partnerId,
            reportId = reportId,
            data = data,
        )

        val expendituresCalculated = reportExpenditureCostCategoryCalculatorService
            .getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId).total

        return identification.fillInCurrentAndPreviousReporting(
            currentReport = expendituresCalculated.currentReport,
            previouslyReported = expendituresCalculated.previouslyReported,
        )
    }

    private fun validateInputs(data: UpdateProjectPartnerReportIdentification) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(data.summary, 5000, "summary"),
            generalValidator.maxLength(data.problemsAndDeviations, 5000, "problemsAndDeviations"),
            generalValidator.maxLength(data.spendingDeviations, 5000, "spendingDeviations"),
            generalValidator.numberBetween(data.nextReportForecast, BigDecimal.ZERO, MAX_NUMBER, "nextReportForecast"),
            *data.targetGroups.mapIndexed { index, it ->
                generalValidator.maxLength(it, 2000, "descriptionOfTheTargetGroup[$index]")
            }.toTypedArray(),
            generalValidator.startDateBeforeEndDate(data.startDate, data.endDate, "startDate", "endDate"),
        )
    }

    private fun validatePeriod(period: Int?, availablePeriods: List<ProjectPartnerReportPeriod>) {
        val validPeriods = availablePeriods.mapTo(HashSet<Int?>()) { it.number } union setOf<Int?>(null)
        if (period !in validPeriods)
            throw InvalidPeriodNumber(periodNumber = period)
    }

}
