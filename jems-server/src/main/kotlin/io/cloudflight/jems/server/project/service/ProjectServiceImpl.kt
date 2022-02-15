package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.model.ProjectForm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ceil

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository,
    private val workPackageRepository: WorkPackageRepository,
    private val getProjectInteractor: GetProjectInteractor,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val generalValidator: GeneralValidatorService
) : ProjectService {

    @Transactional
    @CanUpdateProjectForm
    override fun update(projectId: Long, projectData: InputProjectData): ProjectForm {
        validateProjectData(projectData)
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        validateContractedChanges(projectData, project)
        val periods =
            if (project.projectData?.duration == projectData.duration) project.periods
            else calculatePeriods(projectId, project.call.lengthOfPeriod, projectData.duration)
                .also { periods ->
                    removeNoLongerAvailablePeriods(projectId, maxPeriod = periods.size)
                }

        projectRepo.save(
            project.copy(
                acronym = projectData.acronym!!,
                projectData = projectData.toEntity(project.id),
                priorityPolicy = policyToEntity(projectData.specificObjective, project.call.prioritySpecificObjectives),
                periods = periods
            )
        )
        return getProjectInteractor.getProjectForm(projectId = project.id)
    }

    /**
     * Calculate all necessary project periods with the given periodLength and duration.
     */
    private fun calculatePeriods(
        projectId: Long,
        periodLength: Int,
        duration: Int?
    ): List<ProjectPeriodEntity> {
        if (duration == null || duration < 1)
            return emptyList()

        val count = ceil(duration.toDouble() / periodLength).toInt()

        return (1..count).mapIndexed { index, period ->
            ProjectPeriodEntity(
                id = ProjectPeriodId(projectId = projectId, number = period),
                start = periodLength * index + 1,
                end = if (period == count) duration else periodLength * period
            )
        }
    }

    /**
     * Take policy only if available for this particular Call.
     */
    private fun policyToEntity(
        policy: ProgrammeObjectivePolicy?,
        availablePoliciesForCall: Set<ProgrammeSpecificObjectiveEntity>
    ): ProgrammeSpecificObjectiveEntity? {
        if (policy == null)
            return null
        return availablePoliciesForCall
            .find { it.programmeObjectivePolicy == policy }
            ?: throw ResourceNotFoundException("programmeSpecificObjective")
    }

    private fun validateProjectData(inputProjectData: InputProjectData) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notBlank(inputProjectData.acronym, "acronym"),
            generalValidator.maxLength(inputProjectData.acronym, 25, "acronym"),
            generalValidator.maxLength(inputProjectData.title, 250, "title"),
            generalValidator.numberBetween(inputProjectData.duration, 1, 999, "duration"),
            generalValidator.maxLength(inputProjectData.intro, 2000, "intro"),
        )

    private fun validateContractedChanges(inputProjectData: InputProjectData, project: ProjectEntity) {
        if (!projectVersionPersistence.getAllVersionsByProjectId(project.id)
                .any { it.status == ApplicationStatus.CONTRACTED })
            return
        if (inputProjectData.specificObjective != project.priorityPolicy?.programmeObjectivePolicy)
            throw UpdateRestrictedFieldsWhenProjectContracted()
    }

    private fun removeNoLongerAvailablePeriods(projectId: Long, maxPeriod: Int) {
        workPackageRepository.findAllByProjectId(projectId).forEach {
            it.activities.forEach { it.clearPeriodsBiggerThan(maxPeriod) }
            it.outputs.forEach { it.clearPeriodIfBiggerThan(maxPeriod) }
        }
    }

    private fun WorkPackageActivityEntity.clearPeriodsBiggerThan(maxPeriod: Int) {
        val start = this.startPeriod
        if (start != null && start > maxPeriod)
            this.startPeriod = null
        val end = this.endPeriod
        if (end != null && end > maxPeriod)
            this.endPeriod = null

        this.deliverables.forEach {
            val period = it.startPeriod
            if (period != null && period > maxPeriod)
                it.startPeriod = null
        }
    }

    private fun WorkPackageOutputEntity.clearPeriodIfBiggerThan(maxPeriod: Int) {
        val periodNumber = this.periodNumber
        if (periodNumber != null && periodNumber > maxPeriod)
            this.periodNumber = null
    }
}
