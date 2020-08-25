package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.InputProjectPartner
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.repository.ProjectPartnerRepository
import io.cloudflight.ems.project.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectPartnerServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectRepo: ProjectRepository
) : ProjectPartnerService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputProjectPartner {
        return projectPartnerRepo.findOneById(id)?.toOutputProjectPartner()
            ?: throw ResourceNotFoundException("projectPartner")
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner> {
        return projectPartnerRepo.findAllByProjectId(projectId, page).map { it.toOutputProjectPartner() }
    }

    @Transactional
    override fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartner): OutputProjectPartner {
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")

        return projectPartnerRepo.save(projectPartner.toEntity(project = project)).toOutputProjectPartner()
    }

    @Transactional
    override fun update(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartner {
        val oldProjectPartner = projectPartnerRepo.findById(projectPartner.id).orElseThrow { ResourceNotFoundException("projectPartner") }
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")

        return projectPartnerRepo.save(
            oldProjectPartner.copy(
                name = projectPartner.name!!,
                role = projectPartner.role!!,
                project = project
            )
        ).toOutputProjectPartner()
    }

}
