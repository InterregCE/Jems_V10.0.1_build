package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.project.dto.partner.CreateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectPartner(
    private val persistence: PartnerPersistence,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val generalValidator: GeneralValidatorService
) : CreateProjectPartnerInteractor {

    @CanUpdateProjectForm
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerException::class)
    override fun create(projectId: Long, projectPartner: CreateProjectPartnerRequestDTO): ProjectPartnerDetailDTO {
        validatePartner(projectPartner)
        // to be possible to list all partners for dropdowns we decided to limit total amount of them
        if (projectPartnerRepository.countByProjectId(projectId) >= PartnerPersistenceProvider.MAX_PROJECT_PARTNERS)
            throw MaximumNumberOfPartnersReached()

        // prevent multiple role LEAD_PARTNER entries
        if (projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        // prevent multiple partners with same abbreviation
        validatePartnerAbbreviationUnique(projectId, abbreviation = projectPartner.abbreviation!!)
        return persistence.create(projectId, projectPartner)
    }

    private fun validateLeadPartnerChange(projectId: Long, oldLeadPartnerId: Long?) {
        if (oldLeadPartnerId == null)
            validateOnlyOneLeadPartner(projectId)
        else
            updateOldLeadPartner(projectId, oldLeadPartnerId)
    }

    private fun validatePartnerAbbreviationUnique(projectId: Long, abbreviation: String) {
        val partnerWithSameName = projectPartnerRepository.findFirstByProjectIdAndAbbreviation(projectId, abbreviation)
        if (partnerWithSameName.isPresent) {
            throw PartnerAbbreviationNotUnique(mapOf("abbreviation" to abbreviation))
        }
    }

    /**
     * validate project partner to be saved: only one role LEAD should exist.
     */
    private fun validateOnlyOneLeadPartner(projectId: Long) {
        val projectPartner = projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRoleDTO.LEAD_PARTNER)
        if (projectPartner.isPresent) {
            val currentLead = projectPartner.get()
            throw LeadPartnerAlreadyExists(mapOf("currentLeadId" to currentLead.id.toString(), "currentLeadAbbreviation" to currentLead.abbreviation))
        }
    }

    private fun updateOldLeadPartner(projectId: Long, oldLeadPartnerId: Long) {
        val oldLeadPartner = projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRoleDTO.LEAD_PARTNER)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        if (oldLeadPartner.id == oldLeadPartnerId)
            projectPartnerRepository.save(oldLeadPartner.copy(role = ProjectPartnerRoleDTO.PARTNER))
        else
            throw PartnerIsNotLead()
    }

    private fun validatePartner(inputPartner: CreateProjectPartnerRequestDTO) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNull(inputPartner.role, "role"),
            generalValidator.notBlank(inputPartner.abbreviation, "abbreviation"),
            generalValidator.maxLength(inputPartner.abbreviation, 15, "abbreviation"),
            generalValidator.maxLength(inputPartner.nameInOriginalLanguage, 100, "nameInOriginalLanguage"),
            generalValidator.maxLength(inputPartner.nameInEnglish, 100, "nameInOriginalLanguage"),
            generalValidator.notNull(inputPartner.legalStatusId, "legalStatusId"),
            generalValidator.maxLength(inputPartner.vat, 50, "vat"),
        )
}
