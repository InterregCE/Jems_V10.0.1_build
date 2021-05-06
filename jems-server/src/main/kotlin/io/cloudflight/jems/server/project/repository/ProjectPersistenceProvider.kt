package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectRepository: ProjectRepository,
    private val projectPartnerRepository: ProjectPartnerRepository
) : ProjectPersistence {

    @Transactional(readOnly = true)
    override fun getProject(projectId: Long, version: Int?): Project {
        val project = getProjectOrThrow(projectId)
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                project.toModel()
            },
            // grouped this will be only one result
            previousVersionFetcher = { timestamp ->
                projectRepository.findByIdAsOfTimestamp(projectId, timestamp)
                    .toProjectEntryApplyNonHistoricalData(project)
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getProjectSummary(projectId: Long): ProjectSummary =
        getProjectOrThrow(projectId)!!.let {
            ProjectSummary(it.id, it.acronym, it.currentStatus.status)
        }

    @Transactional(readOnly = true)
    override fun getProjectCallSettings(projectId: Long): ProjectCallSettings =
        getProjectOrThrow(projectId).call.toSettingsModel()

    @Transactional(readOnly = true)
    override fun getProjectUnitCosts(projectId: Long): List<ProgrammeUnitCost> =
        getProjectOrThrow(projectId).call.unitCosts.toModel()

    @Transactional(readOnly = true)
    override fun getProjectIdForPartner(partnerId: Long) =
        projectPartnerRepository.getProjectIdForPartner(partnerId) ?: throw ResourceNotFoundException("ProjectPartner")

    @Transactional(readOnly = true)
    override fun getProjectPeriods(projectId: Long) =
        getProjectOrThrow(projectId).periods.toProjectPeriods()

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

}
