package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.associated_organization.ProjectAssociatedOrganizationRepository
import io.cloudflight.jems.server.project.repository.partner.associated_organization.combineTranslatedValues
import io.cloudflight.jems.server.project.repository.partner.associated_organization.toEntity
import io.cloudflight.jems.server.project.repository.partner.associated_organization.toOutputProjectAssociatedOrganizationDetail
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val MAX_NUMBER_OF_ORGANIZATIONS = 30

@Service
class ProjectAssociatedOrganizationServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectAssociatedOrganizationRepo: ProjectAssociatedOrganizationRepository,
    private val generalValidator: GeneralValidatorService
) : ProjectAssociatedOrganizationService {

    @Transactional
    @ExceptionWrapper(ProjectAssociatedOrganizationException::class)
    override fun create(projectId: Long, associatedOrganization: InputProjectAssociatedOrganization): OutputProjectAssociatedOrganizationDetail {
        validateAssociatedOrganization(associatedOrganization)

        if (projectAssociatedOrganizationRepo.countByProjectId(projectId) >= MAX_NUMBER_OF_ORGANIZATIONS)
            throw MaximumNumberOfOrganizationsReached(MAX_NUMBER_OF_ORGANIZATIONS)

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
    @ExceptionWrapper(ProjectAssociatedOrganizationException::class)
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
    @ExceptionWrapper(ProjectAssociatedOrganizationException::class)
    override fun delete(projectId: Long, associatedOrganizationId: Long) {
        projectAssociatedOrganizationRepo.delete(
            projectAssociatedOrganizationRepo.findFirstByProjectIdAndId(projectId, associatedOrganizationId)
                .orElseThrow { ResourceNotFoundException("projectAssociatedOrganisation") }
        )
        refreshSortNumbers(projectId)
    }

    @Transactional
    @ExceptionWrapper(ProjectAssociatedOrganizationException::class)
    override fun refreshSortNumbers(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "id")
        ))
        val projectAssociatedOrganisations = projectAssociatedOrganizationRepo.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectAssociatedOrganizationRepo.saveAll(projectAssociatedOrganisations)
    }

    private fun validateAssociatedOrganization(inputAssociatedOrganization: InputProjectAssociatedOrganization) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNull(inputAssociatedOrganization.partnerId, "partnerId"),
            generalValidator.notNull(inputAssociatedOrganization.nameInOriginalLanguage, "nameInOriginalLanguage"),
            generalValidator.maxLength(inputAssociatedOrganization.nameInOriginalLanguage, 250, "nameInOriginalLanguage"),
            generalValidator.notNull(inputAssociatedOrganization.nameInEnglish, "nameInOriginalLanguage"),
            generalValidator.maxLength(inputAssociatedOrganization.nameInEnglish, 250, "nameInOriginalLanguage"),
            generalValidator.maxSize(inputAssociatedOrganization.contacts, 2, "contacts"),
        )
}
