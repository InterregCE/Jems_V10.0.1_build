package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorRepository
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectResultPersistenceProvider(
    private val projectRepository: ProjectRepository,
    private val indicatorRepository: ResultIndicatorRepository,
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectResultRepository: ProjectResultRepository
) : ProjectResultPersistence {

    @Transactional(readOnly = true)
    override fun getResultsForProject(projectId: Long, version: String?): List<ProjectResult> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getProjectOrThrow(projectId).results.toModel()
            },
            previousVersionFetcher = { timestamp ->
                projectResultRepository.getProjectResultsByProjectId(projectId, timestamp).toProjectResultHistoricalData()
            }
        ) ?: emptyList()
    }

    @Transactional
    override fun updateResultsForProject(
        projectId: Long,
        projectResults: List<ProjectResult>
    ) : List<ProjectResult> {
        val project = getProjectOrThrow(projectId)

        val resultsUpdated = projectResults.toIndexedEntity(
            projectId = projectId,
            resolveProgrammeResultIndicatorEntity = { getIndicatorOrThrow(it) },
        )
        return projectRepository.save(project.copy(results = resultsUpdated)).results.toModel()
    }

    @Transactional(readOnly = true)
    override fun getAvailablePeriodNumbers(projectId: Long): Set<Int> =
        getProjectOrThrow(projectId).periods.mapTo(HashSet()) { it.id.number }

    private fun getProjectOrThrow(projectId: Long): ProjectEntity =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

    private fun getIndicatorOrThrow(indicatorId: Long?): ResultIndicatorEntity? =
        indicatorId?.let { indicatorRepository.getOne(it) }

}
