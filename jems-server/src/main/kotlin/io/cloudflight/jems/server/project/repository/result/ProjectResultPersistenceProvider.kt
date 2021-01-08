package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorResultRepository
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectResultPersistenceProvider(
    private val projectResultRepository: ProjectResultRepository,
    private val projectRepository: ProjectRepository,
    private val indicatorResultRepository: IndicatorResultRepository,
    private val projectPeriodRepository: ProjectPeriodRepository
) : ProjectResultPersistence {

    @Transactional
    override fun updateProjectResults(
        projectId: Long,
        projectResults: Set<ProjectResultDTO>
    ) : Set<ProjectResultDTO> {
        val project = getProjectOrThrow(projectId)

        project.projectResultEntities.clear()

        projectResults.forEach {
            val indicatorResult =
                if (it.programmeResultIndicatorId != null) indicatorResultRepository.findById(it.programmeResultIndicatorId!!)
                    .orElse(null)
                else null
            val projectPeriod =
                if (it.periodNumber != null)
                    projectPeriodRepository.findByIdProjectIdAndIdNumber(projectId, it.periodNumber!!)
                else null

            val projectResultCreated = projectResultRepository.save(it.toEntity(indicatorResult = indicatorResult, project = project, projectPeriod = projectPeriod))
            val projectResultSavedWithTranslations = projectResultRepository.save(projectResultCreated.copy(translatedValues = combineTranslatedValues(projectResultCreated.id, it.description)))
            project.projectResultEntities.add(projectResultSavedWithTranslations)
        }
        return project.projectResultEntities.toProjectResultSet()

    }

    @Transactional(readOnly = true)
    override fun getProjectResultsForProject(projectId: Long): Set<ProjectResultDTO> {
        val sort = Sort.by(Sort.Direction.ASC, "resultNumber")

        return projectResultRepository.findAllByProjectId(projectId, sort)
            .map { it.ProjectResultDTO() }
            .toSet()
    }

    private fun getProjectOrThrow(projectId: Long): ProjectEntity =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

}
