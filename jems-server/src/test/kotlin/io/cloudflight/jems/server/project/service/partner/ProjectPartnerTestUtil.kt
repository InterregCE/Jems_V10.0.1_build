package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidTranslEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.ZonedDateTime

const val PROJECT_ID = 1L
const val PARTNER_ID = 2L

fun projectPartner(
    id: Long = PARTNER_ID,
    abbreviation: String = "partner",
    role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER,
    department: Set<InputTranslation> = emptySet()
) =
    ProjectPartner(
        id = id,
        abbreviation = abbreviation,
        role = role,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = "12",
        pic = "009",
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        department = department,
        otherIdentifierDescription = emptySet(),
        legalStatusId = 1
    )

fun projectPartnerSummary(
    id: Long = PARTNER_ID,
    abbreviation: String = "partner",
    role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER,
    sortNumber: Int = 0
) =
    ProjectPartnerSummary(
        id = id,
        abbreviation = abbreviation,
        role = role,
        sortNumber,
        country = "AT"
    )

fun projectPartnerDetail(
    id: Long = PARTNER_ID,
    abbreviation: String = "partner",
    role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER,
    contacts: List<ProjectPartnerContact> = emptyList(),
    motivation: ProjectPartnerMotivation? = null,
    department: Set<InputTranslation> = emptySet(),
    address: List<ProjectPartnerAddress> = listOf(ProjectPartnerAddress(type = ProjectPartnerAddressType.Organization, country = "AT")),
    sortNumber: Int = 0
) =
    ProjectPartnerDetail(
        id = id,
        abbreviation = abbreviation,
        role = role,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = "12",
        pic = "009",
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        addresses = address,
        projectId = 1,
        sortNumber = sortNumber,
        department = department,
        otherIdentifierDescription = emptySet(),
        legalStatusId = 1L,
        contacts = contacts,
        motivation = motivation

    )


val legalStatusEntity = ProgrammeLegalStatusEntity(id = 1)

val projectPartnerInclTransl =
    projectPartnerEntity().also {
        it.translatedValues = mutableSetOf(ProjectPartnerTranslEntity(TranslationId(it, SystemLanguage.EN), "test"))
    }


val projectPartnerWithOrganizationEntity = projectPartnerEntity().also {
    it.translatedValues = mutableSetOf(ProjectPartnerTranslEntity(TranslationId(it, SystemLanguage.EN), "test"))
}

fun projectPartnerEntity(id:Long = PARTNER_ID, role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER, abbreviation: String = "partner", sortNumber: Int = 0) = ProjectPartnerEntity(
    id = id,
    project = project,
    abbreviation = abbreviation,
    role = role,
    nameInOriginalLanguage = "test",
    nameInEnglish = "test",
    translatedValues = mutableSetOf(),
    partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
    partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
    nace = NaceGroupLevel.A,
    otherIdentifierNumber = "12",
    pic = "009",
    legalStatus = legalStatusEntity,
    vat = "test vat",
    vatRecovery = ProjectPartnerVatRecovery.Yes,
    addresses = setOf(ProjectPartnerAddressEntity(ProjectPartnerAddressId(PARTNER_ID, ProjectPartnerAddressType.Organization), AddressEntity(country =  "AT"))),
    sortNumber = sortNumber
)


val stateAidEntity = ProjectPartnerStateAidEntity(
    partnerId = PARTNER_ID,
    answer1 = true,
    answer2 = false,
    translatedValues = setOf(
        ProjectPartnerStateAidTranslEntity(
            translationId = TranslationPartnerId(PARTNER_ID, SystemLanguage.EN),
            justification1 = "Is true",
        ),
        ProjectPartnerStateAidTranslEntity(
            translationId = TranslationPartnerId(PARTNER_ID, SystemLanguage.SK),
            justification2 = "Is false",
        ),
    )
)

val stateAid = ProjectPartnerStateAid(
    answer1 = true,
    justification1 = setOf(InputTranslation(SystemLanguage.EN, "Is true")),
    answer2 = false,
    justification2 = setOf(InputTranslation(SystemLanguage.SK, "Is false")),
    answer3 = null,
    answer4 = null
)

val stateAidEmpty = ProjectPartnerStateAid(
    answer1 = null,
    justification1 = emptySet(),
    answer2 = null,
    justification2 = emptySet(),
    answer3 = null,
    justification3 = emptySet(),
    answer4 = null,
    justification4 = emptySet(),
)


class ProjectPartnerTestUtil {

    companion object {

        val userRole = UserRoleEntity(1, "ADMIN")
        val user = UserEntity(
            id = 1,
            name = "Name",
            password = "hash",
            email = "admin@admin.dev",
            surname = "Surname",
            userRole = userRole
        )

        val call = CallEntity(
            id = 1,
            creator = user,
            name = "call",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now(),
            endDateStep1 = null,
            endDate = ZonedDateTime.now(),
            prioritySpecificObjectives = mutableSetOf(),
            strategies = mutableSetOf(),
            isAdditionalFundAllowed = false,
            funds = mutableSetOf(),
            lengthOfPeriod = 1
        )
        val projectStatus = ProjectStatusHistoryEntity(
            status = ApplicationStatus.APPROVED,
            user = user,
            updated = ZonedDateTime.now()
        )
        val project = ProjectEntity(
            id = 1,
            acronym = "acronym",
            call = call,
            applicant = user,
            currentStatus = projectStatus,
        )

        val userSummary = UserSummary(
            id = 1,
            email = user.email,
            name = user.name,
            surname = user.surname,
            userRole = UserRoleSummary(id = 1, name = "ADMIN"),
        )
    }

}
