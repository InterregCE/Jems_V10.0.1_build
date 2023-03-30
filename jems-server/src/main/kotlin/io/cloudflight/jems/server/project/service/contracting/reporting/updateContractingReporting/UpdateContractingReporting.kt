package io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectReportingSchedule
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.contracting.toLimits
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UpdateContractingReporting(
    private val contractingReportingPersistence: ContractingReportingPersistence,
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val generalValidator: GeneralValidatorService,
    private val contractingValidator: ContractingValidator,
    private val projectReportPersistence: ProjectReportPersistence
): UpdateContractingReportingInteractor {

    companion object {
        private const val MAX_DEADLINES_AMOUNT = 50
    }

    @CanEditProjectReportingSchedule
    @Transactional
    @ExceptionWrapper(UpdateContractingReportingException::class)
    override fun updateReportingSchedule(
        projectId: Long,
        deadlines: Collection<ProjectContractingReportingSchedule>,
    ): List<ProjectContractingReportingSchedule> {
        contractingValidator.validateSectionLock(ProjectContractingSection.ProjectReportingSchedule, projectId)
        val lastApprovedVersion = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val project = projectPersistence.getProject(projectId, lastApprovedVersion)
        if (project.projectStatus.status.hasNotBeenApprovedYet())
            throw ProjectHasNotBeenApprovedYet()

        val monitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
        if (monitoring.startDate == null)
            throw ContractingStartDateIsMissing()

        val periods = project.periods.associateBy { it.number }
        validateInputData(deadlines, periods, monitoring.startDate, projectId)

        return contractingReportingPersistence.updateContractingReporting(
            projectId = projectId,
            deadlines = deadlines,
        )
    }

    @Transactional
    override fun checkNoLongerAvailablePeriodsAndDatesToRemove(projectId: Long) {
        val allVersions = projectVersionPersistence.getAllVersionsByProjectId(projectId)
        val allApprovedVersions = allVersions.filter { it.status.isApproved() }
        if (allApprovedVersions.size > 1) {
            val previousApprovedVersion = allApprovedVersions[1]
            val previousDuration = projectPersistence.getProject(projectId, previousApprovedVersion.version).duration
            val currentDuration = projectPersistence.getProject(projectId).duration
            if (currentDuration == null || (previousDuration != null &&  currentDuration < previousDuration)) {
                val newMaxDuration = currentDuration ?: 0
                val schedules = contractingReportingPersistence.getScheduleIdsWhosePeriodsAndDatesNotProper(projectId, newMaxDuration)
                if (schedules.isNotEmpty()) {
                    contractingReportingPersistence.clearPeriodAndDatesFor(schedules)
                }
            }
        }
    }

    private fun validateInputData(
        deadlines: Collection<ProjectContractingReportingSchedule>,
        periods: Map<Int, ProjectPeriod>,
        startDate: LocalDate,
        projectId: Long
    ) {
        if (deadlines.size > MAX_DEADLINES_AMOUNT)
            throw MaxAmountOfDeadlinesReached(MAX_DEADLINES_AMOUNT)

        validatePeriods(deadlines, periods)
        validateDates(deadlines, periods, startDate)
        validateComments(deadlines)

        val linkedDeadlines = projectReportPersistence.getReportLinkedDeadlineIdsWithIsReportSubmittedForProject(projectId)
        if (linkedDeadlines.map { it.first }.minus(deadlines.map { it.id }).isNotEmpty())
            throw LinkedDeadlineDeletionException()

        val submittedDeadlineIds = linkedDeadlines.filter { it.second }.map { it.first }
        val existingDeadlines = contractingReportingPersistence.getContractingReporting(projectId)
        existingDeadlines.filter { submittedDeadlineIds.contains(it.id) }.forEach { existing ->
            val deadline = deadlines.first { it.id == existing.id }
            if (deadline.date != existing.date || deadline.periodNumber != existing.periodNumber || deadline.type != existing.type)
                throw LinkedDeadlineUpdateException()
        }
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
        val invalidDates = deadlines.filter { it.date!!.isBefore(periodLimits.startLimit(it.periodNumber!!)) }
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
