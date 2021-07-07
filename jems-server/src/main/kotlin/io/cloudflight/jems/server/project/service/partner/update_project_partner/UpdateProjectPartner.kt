package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartnerBase
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartner(
    private val persistence: PartnerPersistence,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val generalValidator: GeneralValidatorService
) : UpdateProjectPartnerInteractor {

    @CanUpdateProjectPartnerBase
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerException::class)
    override fun update(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail {
        validatePartner(projectPartner)
        val oldProjectPartner = getPartnerOrThrow(projectPartner.id)
        val projectId = oldProjectPartner.project.id
        val makingThisLead = !oldProjectPartner.role.isLead && projectPartner.role!!.isLead
        if (makingThisLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        if (oldProjectPartner.abbreviation != projectPartner.abbreviation) {
            validatePartnerAbbreviationUnique(projectId, abbreviation = projectPartner.abbreviation!!)
        }
        return persistence.update(projectPartner)
    }


    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerAddressesException::class)
    override fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail =
        persistence.updatePartnerAddresses(partnerId, addresses)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerContactsException::class)
    override fun updatePartnerContacts(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail =
        persistence.updatePartnerContacts(partnerId, contacts)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerMotivationException::class)
    override fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail =
        persistence.updatePartnerMotivation(partnerId, motivation)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerMotivationException::class)
    override fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid {
        TODO("Not yet implemented")
    }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity {
        return projectPartnerRepository.findById(partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
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
        val projectPartner = projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER)
        if (projectPartner.isPresent) {
            val currentLead = projectPartner.get()
            throw LeadPartnerAlreadyExists(mapOf("currentLeadId" to currentLead.id.toString(), "currentLeadAbbreviation" to currentLead.abbreviation))
        }
    }

    private fun updateOldLeadPartner(projectId: Long, oldLeadPartnerId: Long) {
        val oldLeadPartner = projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        if (oldLeadPartner.id == oldLeadPartnerId)
            projectPartnerRepository.save(oldLeadPartner.copy(role = ProjectPartnerRole.PARTNER))
        else
            throw PartnerIsNotLead()
    }

    private fun validatePartner(inputPartner: InputProjectPartnerUpdate) =
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
