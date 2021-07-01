package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.controller.toDto
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ceil

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository,
    private val getProjectInteractor: GetProjectInteractor,
    private val generalValidator: GeneralValidatorService
) : ProjectService {

    @Transactional
    override fun update(id: Long, projectData: InputProjectData): ProjectDetailDTO {
        validateProjectData(projectData)
        val project = projectRepo.findById(id).orElseThrow { ResourceNotFoundException("project") }
        val periods =
            if (project.projectData?.duration == projectData.duration) project.periods
            else calculatePeriods(id, project.call.lengthOfPeriod, projectData.duration)

        projectRepo.save(
            project.copy(
                acronym = projectData.acronym!!,
                projectData = projectData.toEntity(project.id),
                priorityPolicy = policyToEntity(projectData.specificObjective, project.call.prioritySpecificObjectives),
                periods = periods
            )
        )
        return getProjectInteractor.getProject(projectId = project.id).toDto()
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
}
