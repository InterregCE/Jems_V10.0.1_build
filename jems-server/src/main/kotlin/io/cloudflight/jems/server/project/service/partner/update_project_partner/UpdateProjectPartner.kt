package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.nuts.service.NutsService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartnerBase
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartner(
    private val persistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val generalValidator: GeneralValidatorService,
    private val nutsService: NutsService
) : UpdateProjectPartnerInteractor {

    @CanUpdateProjectPartnerBase
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerException::class)
    override fun update(projectPartner: ProjectPartner): ProjectPartnerDetail =
        ifProjectPartnerIsValid(projectPartner).run {
            val oldPartner = persistence.getById(projectPartner.id!!)

            if (oldPartner.role != ProjectPartnerRole.LEAD_PARTNER && projectPartner.role!!.isLead)
                persistence.changeRoleOfLeadPartnerToPartnerIfItExists(oldPartner.projectId)

            if (oldPartner.abbreviation != projectPartner.abbreviation)
                persistence.throwIfPartnerAbbreviationAlreadyExists(oldPartner.projectId, projectPartner.abbreviation!!)

            persistence.update(projectPartner, shouldResortPartnersByRole(projectPartner.id))
        }


    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerAddressesException::class)
    override fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddress>): ProjectPartnerDetail =
        addresses.any { !nutsService.validateAddress(it.country, it.nutsRegion2, it.nutsRegion3)}.let { isAnyAddressInvalid ->
            if(isAnyAddressInvalid) throw InvalidProjectPartnerAddressesException()
            persistence.updatePartnerAddresses(partnerId, addresses)
        }

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerContactsException::class)
    override fun updatePartnerContacts(partnerId: Long, contacts: Set<ProjectPartnerContact>): ProjectPartnerDetail =
        persistence.updatePartnerContacts(partnerId, contacts)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerMotivationException::class)
    override fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivation): ProjectPartnerDetail =
        persistence.updatePartnerMotivation(partnerId, motivation)


    private fun ifProjectPartnerIsValid(partner: ProjectPartner) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNull(partner.id, "id"),
            generalValidator.notNull(partner.role, "role"),
            generalValidator.notBlank(partner.abbreviation, "abbreviation"),
            generalValidator.maxLength(partner.abbreviation, 15, "abbreviation"),
            generalValidator.maxLength(partner.nameInOriginalLanguage, 100, "nameInOriginalLanguage"),
            generalValidator.maxLength(partner.nameInEnglish, 100, "nameInEnglish"),
            generalValidator.notNull(partner.legalStatusId, "legalStatusId"),
            generalValidator.maxLength(partner.otherIdentifierNumber, 50, "otherIdentifierNumber"),
            generalValidator.maxLength(partner.otherIdentifierDescription, 100, "otherIdentifierDescription"),
            generalValidator.exactLength(partner.pic, 9, "pic"),
            generalValidator.onlyDigits(partner.pic, "pic"),
            generalValidator.maxLength(partner.vat, 50, "vat"),
        )

    private fun shouldResortPartnersByRole(partnerId: Long) =
        projectPersistence.getProjectSummary(persistence.getProjectIdForPartnerId(partnerId)).let { projectSummary ->
            projectSummary.status.isModifiableStatusBeforeApproved()
        }
}
