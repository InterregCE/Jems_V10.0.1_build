package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectAssociatedOrganizationServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectRepo: ProjectRepository,
    private val projectAssociatedOrganizationRepo: ProjectAssociatedOrganizationRepository
) : ProjectAssociatedOrganizationService {
    @Transactional(readOnly = true)
    override fun getById(projectId: Long, id: Long): OutputProjectAssociatedOrganizationDetail {
        return projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, id).map { it.toOutputProjectAssociatedOrganizationDetail() }
            .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectAssociatedOrganization> {
        return projectAssociatedOrganizationRepo.findAllByProjectId(projectId, page).map { it.toOutputProjectAssociatedOrganization() }
    }

    @Transactional
    override fun create(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationCreate): OutputProjectAssociatedOrganizationDetail {
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        val partner = projectPartnerRepo.findById(associatedOrganization.partnerId).orElseThrow { ResourceNotFoundException("projectPartner") }

        val savedAssociatedOrganisation = projectAssociatedOrganizationRepo.save(associatedOrganization.toEntity(project = project, partner = partner))

        val updatedAssociatedOrganisation = projectAssociatedOrganizationRepo.save(
            savedAssociatedOrganisation.copy(
                organizationAddress = associatedOrganization.organizationAddress?.toEntity(savedAssociatedOrganisation),
                associatedOrganizationContacts = associatedOrganization.associatedOrganizationContacts?.map { it.toAssociatedOrganizationContact(savedAssociatedOrganisation) }?.toHashSet()))

        this.updateSort(projectId);

        return updatedAssociatedOrganisation.toOutputProjectAssociatedOrganizationDetail()
    }

    @Transactional
    override fun update(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationUpdate): OutputProjectAssociatedOrganizationDetail {
        val oldAssociatedOrganisation = projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.id)
            .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
        val projectPartner = projectPartnerRepo.findById(associatedOrganization.partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        val savedAssociatedOrganisation = projectAssociatedOrganizationRepo.save(
            oldAssociatedOrganisation.copy(
                partner = projectPartner,
                organizationAddress = associatedOrganization.organizationAddress?.toEntity(oldAssociatedOrganisation),
                associatedOrganizationContacts = associatedOrganization.associatedOrganizationContacts?.map { it.toAssociatedOrganizationContact(oldAssociatedOrganisation) }?.toHashSet()
            )
        )

        this.updateSort(projectId);

        return savedAssociatedOrganisation.toOutputProjectAssociatedOrganizationDetail()
    }

    /**
     * sets or updates the sort number for all associated organizations for the specified project.
     */
    protected fun updateSort(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "id")
        ))
        val projectAssociatedOrganisations = projectAssociatedOrganizationRepo.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectAssociatedOrganizationRepo.saveAll(projectAssociatedOrganisations)
    }

    @Transactional
    override fun delete(projectId: Long, associatedOrganizationId: Long) {
        this.projectAssociatedOrganizationRepo.deleteById(associatedOrganizationId)
        this.updateSort(projectId)
    }
}
