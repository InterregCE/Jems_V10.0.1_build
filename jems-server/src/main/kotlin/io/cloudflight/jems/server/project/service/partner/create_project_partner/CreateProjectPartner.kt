package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val MAX_NUMBER_OF_PROJECT_PARTNERS = 30

@Service
class CreateProjectPartner(
    private val persistence: PartnerPersistence,
    private val generalValidator: GeneralValidatorService
) : CreateProjectPartnerInteractor {

    @CanUpdateProjectForm
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerException::class)
    override fun create(projectId: Long, projectPartner: ProjectPartner): ProjectPartnerDetail =
        ifProjectPartnerIsValid(projectPartner).run {

            if (persistence.countByProjectId(projectId) >= MAX_NUMBER_OF_PROJECT_PARTNERS)
                throw MaximumNumberOfPartnersReached()

            if (projectPartner.role!!.isLead)
                persistence.changeRoleOfLeadPartnerToPartnerIfItExists(projectId)

            persistence.throwIfPartnerAbbreviationAlreadyExists(projectId, projectPartner.abbreviation!!)

            persistence.create(projectId, projectPartner)
        }


    private fun ifProjectPartnerIsValid(partner: ProjectPartner) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.nullOrZero(partner.id, "id"),
            generalValidator.notNull(partner.role, "role"),
            generalValidator.notBlank(partner.abbreviation, "abbreviation"),
            generalValidator.maxLength(partner.abbreviation, 15, "abbreviation"),
            generalValidator.maxLength(partner.nameInOriginalLanguage, 100, "nameInOriginalLanguage"),
            generalValidator.maxLength(partner.nameInEnglish, 100, "nameInOriginalLanguage"),
            generalValidator.notNull(partner.legalStatusId, "legalStatusId"),
            generalValidator.maxLength(partner.otherIdentifierNumber, 50, "otherIdentifierNumber"),
            generalValidator.maxLength(partner.otherIdentifierDescription, 100, "otherIdentifierDescription"),
            generalValidator.maxLength(partner.pic, 9, "pic"),
            generalValidator.onlyDigits(partner.pic, "pic"),
            generalValidator.maxLength(partner.vat, 50, "vat"),
        )
}
