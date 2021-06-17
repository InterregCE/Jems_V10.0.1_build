package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectAssociatedOrganizationServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectAssociatedOrganizationRepo: ProjectAssociatedOrganizationRepository,
    private val generalValidator: GeneralValidatorService
) : ProjectAssociatedOrganizationService {

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long): Iterable<OutputProjectAssociatedOrganizationDetail> {
        return projectAssociatedOrganizationRepo.findAllByProjectId(projectId)
            .map { it.toOutputProjectAssociatedOrganizationDetail() }
    }

    @Transactional
    override fun create(projectId: Long, associatedOrganization: InputProjectAssociatedOrganization): OutputProjectAssociatedOrganizationDetail {
        validateAssociatedOrganization(associatedOrganization)
        val partner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        var savedEntity = projectAssociatedOrganizationRepo.save(
            associatedOrganization.toEntity(partner = partner)
        )

        savedEntity = projectAssociatedOrganizationRepo.save(savedEntity.copy(
            contacts = associatedOrganization.contacts.toEntity(savedEntity.id),
            addresses = associatedOrganization.address.toEntity(savedEntity.id),
            translatedValues = associatedOrganization.combineTranslatedValues(savedEntity.id)
        ))
        refreshSortNumbers(projectId)
        return savedEntity.toOutputProjectAssociatedOrganizationDetail()
    }

    @Transactional
    override fun update(projectId: Long, associatedOrganization: InputProjectAssociatedOrganization): OutputProjectAssociatedOrganizationDetail {
        validateAssociatedOrganization(associatedOrganization)
        val oldAssociatedOrganisation = projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.id!!)
            .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
        val projectPartner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, associatedOrganization.partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        return projectAssociatedOrganizationRepo.save(
            oldAssociatedOrganisation.copy(
                project = projectPartner.project,
                partner = projectPartner,
                nameInOriginalLanguage = associatedOrganization.nameInOriginalLanguage,
                nameInEnglish = associatedOrganization.nameInEnglish,
                addresses = associatedOrganization.address.toEntity(oldAssociatedOrganisation.id),
                contacts = associatedOrganization.contacts.toEntity(oldAssociatedOrganisation.id),
                translatedValues = associatedOrganization.combineTranslatedValues(oldAssociatedOrganisation.id)
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

    private fun validateAssociatedOrganization(inputAssociatedOrganizaion: InputProjectAssociatedOrganization) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNull(inputAssociatedOrganizaion.partnerId, "partnerId"),
            generalValidator.notNull(inputAssociatedOrganizaion.nameInOriginalLanguage, "nameInOriginalLanguage"),
            generalValidator.maxLength(inputAssociatedOrganizaion.nameInOriginalLanguage, 100, "nameInOriginalLanguage"),
            generalValidator.notNull(inputAssociatedOrganizaion.nameInEnglish, "nameInOriginalLanguage"),
            generalValidator.maxLength(inputAssociatedOrganizaion.nameInEnglish, 100, "nameInOriginalLanguage"),
            generalValidator.maxSize(inputAssociatedOrganizaion.contacts, 2, "contacts"),
        )
}
