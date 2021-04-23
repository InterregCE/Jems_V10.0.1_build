package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.project.service.ProjectDescriptionService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectDataProviderImpl(
    private val projectService: ProjectService,
    private val projectDescriptionService: ProjectDescriptionService,
    private val workPackagePersistence: WorkPackagePersistence,
    private val resultPersistence: ProjectResultPersistence
) : ProjectDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectDataProviderImpl::class.java)
        private const val MAX_WORK_PACKAGES_PER_PROJECT = 20
    }

    @Transactional(readOnly = true)
    override fun getProjectDataForProjectId(projectId: Long): ProjectData {
        val sectionA = projectService.getById(projectId).projectData?.toDataModel()
        val workPackages = workPackagePersistence.getRichWorkPackagesByProjectId(projectId, PageRequest.of(0, MAX_WORK_PACKAGES_PER_PROJECT)).content.toDataModel()
        val results = resultPersistence.getResultsForProject(projectId).toResultDataModel()
        val sectionC = projectDescriptionService.getProjectDescription(projectId).toDataModel(workPackages, results)

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(sectionA, sectionC)
    }
}