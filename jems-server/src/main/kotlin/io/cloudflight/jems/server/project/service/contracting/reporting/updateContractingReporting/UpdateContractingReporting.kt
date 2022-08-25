package io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting

import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingReporting
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.contracting.toLimits
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UpdateContractingReporting(
    private val contractingReportingPersistence: ContractingReportingPersistence,
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val generalValidator: GeneralValidatorService,
): UpdateContractingReportingInteractor {

    companion object {
        private const val MAX_DEADLINES_AMOUNT = 50
    }

    @CanUpdateProjectContractingReporting
    @Transactional
    @ExceptionWrapper(UpdateContractingReportingException::class)
    override fun updateReportingSchedule(
        projectId: Long,
        deadlines: Collection<ProjectContractingReportingSchedule>,
    ): List<ProjectContractingReportingSchedule> {
        val lastApprovedVersion = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val project = projectPersistence.getProject(projectId, lastApprovedVersion)
        if (project.projectStatus.status.hasNotBeenApprovedYet())
            throw ProjectHasNotBeenApprovedYet()

        val monitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
        if (monitoring.startDate == null)
            throw ContractingStartDateIsMissing()

        val periods = project.periods.associateBy { it.number }
        validateInputData(deadlines, periods, monitoring.startDate)

        return contractingReportingPersistence.updateContractingReporting(
            projectId = projectId,
            deadlines = deadlines,
        )
    }

    @Transactional
    override fun clearNoLongerAvailablePeriodsAndDates(projectId: Long, newMaxDuration: Int) {
        val schedules = contractingReportingPersistence.getScheduleIdsWhosePeriodsAndDatesNotProper(projectId, newMaxDuration)
        if (schedules.isNotEmpty()) {
            contractingReportingPersistence.clearPeriodAndDatesFor(schedules)
        }
    }

    private fun validateInputData(
        deadlines: Collection<ProjectContractingReportingSchedule>,
        periods: Map<Int, ProjectPeriod>,
        startDate: LocalDate,
    ) {
        if (deadlines.size > MAX_DEADLINES_AMOUNT)
            throw MaxAmountOfDeadlinesReached(MAX_DEADLINES_AMOUNT)

        validatePeriods(deadlines, periods)
        validateDates(deadlines, periods, startDate)
        validateComments(deadlines)
    }

    private fun validatePeriods(
        deadlines: Collection<ProjectContractingReportingSchedule>,
        periods: Map<Int, ProjectPeriod>,
    ) {
        if (deadlines.any { it.periodNumber == null })
            throw EmptyPeriodNumber()
        val invalidPeriods = deadlines.mapTo(HashSet()) { it.periodNumber!! }
        invalidPeriods.removeAll(periods.keys)

        if (invalidPeriods.isNotEmpty())
            throw InvalidPeriodNumbers(invalidPeriods)
    }
    private fun validateDates(
        deadlines: Collection<ProjectContractingReportingSchedule>,
        periods: Map<Int, ProjectPeriod>,
        startDate: LocalDate,
    ) {
        if (deadlines.any { it.date == null })
            throw EmptyDeadlineDate()
        val periodLimits = periods.mapValues { it.value.toLimits(startDate) }
        val invalidDates = deadlines.filter {
            it.date!!.isBefore(periodLimits.startLimit(it.periodNumber!!)) || it.date!!.isAfter(periodLimits.endLimit(it.periodNumber))
        }
        if (invalidDates.isNotEmpty())
            throw DeadlinesDoNotFitPeriod(invalidDates.map { Triple(it, periodLimits.startLimit(it.periodNumber!!), periodLimits.endLimit(it.periodNumber)) })
    }

    fun validateComments(deadlines: Collection<ProjectContractingReportingSchedule>){
        generalValidator.throwIfAnyIsInvalid(
            *deadlines.mapIndexed { index, it -> generalValidator.maxLength(it.comment, 2000, "comment[$index]") }.toTypedArray(),
        )
    }

    private fun Map<Int, Pair<LocalDate, LocalDate>>.startLimit(periodNumber: Int) = get(periodNumber)!!.first
    private fun Map<Int, Pair<LocalDate, LocalDate>>.endLimit(periodNumber: Int) = get(periodNumber)!!.second
}
