package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectAssociatedOrganizationServiceImpl(
        private val projectPartnerRepo: ProjectPartnerRepository,
        private val projectAssociatedOrganizationRepo: ProjectAssociatedOrganizationRepository
) : ProjectAssociatedOrganizationService {

    @Transactional(readOnly = true)
    override fun getById(projectId: Long, id: Long): OutputProjectAssociatedOrganizationDetail {
        return projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, id)
            .map { it.toOutputProjectAssociatedOrganizationDetail() }
            .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectAssociatedOrganization> {
        return projectAssociatedOrganizationRepo.findAllByProjectId(projectId, page)
            .map { it.toOutputProjectAssociatedOrganization() }
    }

    @Transactional
    override fun create(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationCreate): OutputProjectAssociatedOrganizationDetail {
        val partner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        var savedEntity = projectAssociatedOrganizationRepo.save(
            associatedOrganization.toEntity(partner = partner)
        )

        savedEntity = projectAssociatedOrganizationRepo.save(savedEntity.copy(
            contacts = associatedOrganization.contacts.toEntity(savedEntity.id!!),
            addresses = associatedOrganization.address.toEntity(savedEntity.id!!)
        ))
        refreshSortNumbers(projectId)
        return savedEntity.toOutputProjectAssociatedOrganizationDetail()
    }

    @Transactional
    override fun update(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationUpdate): OutputProjectAssociatedOrganizationDetail {
        val oldAssociatedOrganisation = projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.id)
            .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
        val projectPartner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        return projectAssociatedOrganizationRepo.save(
            oldAssociatedOrganisation.copy(
                project = projectPartner.project,
                partner = projectPartner,
                nameInOriginalLanguage = associatedOrganization.nameInOriginalLanguage,
                nameInEnglish = associatedOrganization.nameInEnglish,
                addresses = associatedOrganization.address.toEntity(oldAssociatedOrganisation.id!!),
                contacts = associatedOrganization.contacts.toEntity(oldAssociatedOrganisation.id!!)
            )
        ).toOutputProjectAssociatedOrganizationDetail()
    }

    @Transactional
    override fun delete(projectId: Long, associatedOrganizationId: Long) {
        projectAssociatedOrganizationRepo.delete(
            projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, associatedOrganizationId)
                .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
        )
        refreshSortNumbers(projectId)
    }

    @Transactional
    override fun refreshSortNumbers(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "id")
        ))
        val projectAssociatedOrganisations = projectAssociatedOrganizationRepo.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectAssociatedOrganizationRepo.saveAll(projectAssociatedOrganisations)
    }
}
