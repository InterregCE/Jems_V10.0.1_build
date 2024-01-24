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
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.common.toLimits
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
    private val projectReportPersistence: ProjectReportPersistence,
    private val certificatePersistence: ProjectReportCertificatePersistence
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

        val startDate = contractingMonitoringPersistence.getContractingMonitoring(projectId).startDate
            ?: throw ContractingStartDateIsMissing()

        val periods = project.periods.associateBy { it.number }
        val existingDeadlines = contractingReportingPersistence.getContractingReporting(projectId)

        validateInputData(deadlines, existingDeadlines.associateBy { it.id }, periods, startDate, projectId)
        deselectCertificatesIfFinancePartExcluded(deadlines, existingDeadlines)

        return contractingReportingPersistence.updateContractingReporting(
            projectId = projectId,
            deadlines = deadlines.fillMissingNumbers(ignoreIds = existingDeadlines.map { it.id }.toSet()),
        )
    }

    private fun deselectCertificatesIfFinancePartExcluded(
        deadlines: Collection<ProjectContractingReportingSchedule>,
        existingDeadlines: List<ProjectContractingReportingSchedule>,
    ) {
        val existingFinanceIds = existingDeadlines.filter { it.type.hasFinance() }.mapTo(HashSet()) { it.id }
        val toBeSavedFinanceIds = deadlines.filter { it.type.hasFinance() }.mapTo(HashSet()) { it.id }

        val lostFinanceDeadlineIds = existingFinanceIds.minus(toBeSavedFinanceIds)
        if (lostFinanceDeadlineIds.isNotEmpty())
            certificatePersistence.deselectAllCertificatesForDeadlines(deadlineIds = lostFinanceDeadlineIds)
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
        oldDeadlines: Map<Long, ProjectContractingReportingSchedule>,
        periods: Map<Int, ProjectPeriod>,
        startDate: LocalDate,
        projectId: Long
    ) {
        if (deadlines.size > MAX_DEADLINES_AMOUNT)
            throw MaxAmountOfDeadlinesReached(MAX_DEADLINES_AMOUNT)

        validatePeriods(deadlines.filter { it.linkedSubmittedProjectReportNumbers.isEmpty() }, periods)
        validateDates(deadlines.filter { it.linkedSubmittedProjectReportNumbers.isEmpty() }, periods, startDate)
        validateComments(deadlines)

        val linkedDeadlines = projectReportPersistence.getDeadlinesWithLinkedReportStatus(projectId)
        val deletedLinkedDeadlineIds = linkedDeadlines.keys.minus(deadlines.ids())
        if (deletedLinkedDeadlineIds.isNotEmpty())
            throw LinkedDeadlineDeletedException(deletedLinkedDeadlineIds)

        val submittedDeadlineIds = linkedDeadlines.filter { it.value.isClosed() }.keys

        deadlines.filter { it.id in submittedDeadlineIds }.forEach { new ->
            if (forbiddenChangeAfterSubmission(new = new, old = oldDeadlines[new.id]!!))
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

    private fun Collection<ProjectContractingReportingSchedule>.ids() = mapTo(HashSet()) { it.id }

    private fun Collection<ProjectContractingReportingSchedule>.fillMissingNumbers(
        ignoreIds: Set<Long>,
    ): List<ProjectContractingReportingSchedule> {
        var nextNumber = this.maxOfOrNull { it.number + 1 } ?: 1
        return map { deadline ->
            if (deadline.id !in ignoreIds) {
                deadline.number = nextNumber
                nextNumber++
            }
            return@map deadline
        }
    }

    private fun forbiddenChangeAfterSubmission(
        old: ProjectContractingReportingSchedule,
        new: ProjectContractingReportingSchedule,
    ) = old.date != new.date || old.periodNumber != new.periodNumber || old.type != new.type
}
