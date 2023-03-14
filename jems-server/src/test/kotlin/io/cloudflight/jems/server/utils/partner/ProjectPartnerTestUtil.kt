package io.cloudflight.jems.server.utils.partner

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.PartnerDetailRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidTranslEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectContactType
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
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
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

const val PROJECT_ID = 1L
const val PARTNER_ID = 2L
val CREATED_AT: ZonedDateTime = ZonedDateTime.of(2020,1,10,10,10,10,10, ZoneOffset.UTC)
val CREATED_AT_TIMESTAMP: Timestamp = Timestamp.valueOf(LocalDateTime.of(2020,1,10,10,10,10,10))

fun projectSummary(status: ApplicationStatus = ApplicationStatus.DRAFT) = ProjectSummary(
    id = PROJECT_ID,
    customIdentifier = "01",
    callId = 1L,
    callName = "",
    acronym = "project acronym",
    status = status
)

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
        active = true,
        abbreviation = abbreviation,
        role = role,
        sortNumber = sortNumber,
        country = "AT"
    )

fun projectPartnerDetail(
    id: Long = PARTNER_ID,
    abbreviation: String = "partner",
    role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER,
    contacts: List<ProjectPartnerContact> = emptyList(),
    motivation: ProjectPartnerMotivation? = null,
    department: Set<InputTranslation> = emptySet(),
    address: List<ProjectPartnerAddress> = listOf(
        ProjectPartnerAddress(
            type = ProjectPartnerAddressType.Organization,
            country = "AT"
        )
    ),
    sortNumber: Int = 0
) =
    ProjectPartnerDetail(
        id = id,
        active = true,
        abbreviation = abbreviation,
        role = role,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        createdAt = CREATED_AT,
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

fun projectPartnerWithOrganizationEntity(sortNumber: Int = 0) = projectPartnerEntity(sortNumber = sortNumber).also {
    it.translatedValues.add(ProjectPartnerTranslEntity(TranslationId(it, SystemLanguage.EN), "test"))
}

fun projectPartnerEntity(
    id: Long = PARTNER_ID,
    role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER,
    abbreviation: String = "partner",
    sortNumber: Int = 0
) = ProjectPartnerEntity(
    id = id,
    project = project,
    abbreviation = abbreviation,
    role = role,
    nameInOriginalLanguage = "test",
    createdAt = CREATED_AT,
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
    addresses = setOf(
        ProjectPartnerAddressEntity(
            ProjectPartnerAddressId(
                PARTNER_ID,
                ProjectPartnerAddressType.Organization
            ), AddressEntity(country = "AT")
        )
    ),
    sortNumber = sortNumber
)

fun userSummary(id: Long, roleId: Long) = UserSummary(
    id = id,
    email = "user@email.com",
    name = "",
    surname = "",
    userRole = UserRoleSummary(roleId, "", false),
    userStatus = UserStatus.ACTIVE,
)

fun partnerDetailRows(): List<PartnerDetailRow> =
        listOf(
            object : PartnerDetailRow {
                override val id = PARTNER_ID
                override val projectId = PROJECT_ID
                override val abbreviation = "partner"
                override val active = true
                override val role = ProjectPartnerRole.LEAD_PARTNER
                override val sortNumber = 0
                override val createdAt = CREATED_AT_TIMESTAMP
                override val nameInOriginalLanguage = "test"
                override val nameInEnglish = "test"
                override val partnerType = ProjectTargetGroup.BusinessSupportOrganisation
                override val partnerSubType = PartnerSubType.LARGE_ENTERPRISE
                override val nace = NaceGroupLevel.A
                override val otherIdentifierNumber = "12"
                override val pic = "009"
                override val legalStatusId = 1L
                override val vat = "test vat"
                override val vatRecovery = ProjectPartnerVatRecovery.Yes
                override val language: SystemLanguage? = null

                override val department: String? = null
                override val otherIdentifierDescription: String? = null

                override val addressType = ProjectPartnerAddressType.Organization
                override val country = "AT"
                override val countryCode = "AT"
                override val nutsRegion2: String? = null
                override val nutsRegion2Code: String? = null
                override val nutsRegion3: String? = null
                override val nutsRegion3Code: String? = null
                override val street: String? = null
                override val houseNumber: String? = null
                override val postalCode: String? = null
                override val city: String? = null
                override val homepage: String? = null

                override val contactType: ProjectContactType? = null
                override val title: String? = null
                override val firstName: String? = null
                override val lastName: String? = null
                override val email: String? = null
                override val telephone: String? = null

                override val motivationRowLanguage: SystemLanguage? = null
                override val organizationRelevance: String? = null
                override val organizationRole: String? = null
                override val organizationExperience: String? = null
            }
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
    answer4 = null,
    stateAidScheme = null
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
    stateAidScheme = null
)

val activitySummary = WorkPackageActivitySummary(
    activityId = 3L,
    workPackageNumber = 10,
    activityNumber = 3
)

val programmeStateAidEntity = ProgrammeStateAidEntity(
    id = 2,
    measure = ProgrammeStateAidMeasure.OTHER_1,
    maxIntensity = BigDecimal.TEN,
    threshold = BigDecimal.TEN,
    schemeNumber = "NR1",
    translatedValues = mutableSetOf()
)

val stateAidActivity = ProjectPartnerStateAid(
    answer1 = true,
    justification1 = setOf(InputTranslation(SystemLanguage.EN, "justification1")),
    answer2 = false,
    justification2 = setOf(InputTranslation(SystemLanguage.EN, "justification2")),
    answer3 = null,
    answer4 = null,
    activities = listOf(activitySummary),
    stateAidScheme = ProgrammeStateAid(
        id = 2,
        measure = ProgrammeStateAidMeasure.OTHER_1,
        name = emptySet(),
        abbreviatedName = emptySet(),
        schemeNumber = programmeStateAidEntity.schemeNumber,
        maxIntensity = programmeStateAidEntity.maxIntensity,
        threshold = programmeStateAidEntity.threshold,
        comments = emptySet()
    )
)

val activityEntity = WorkPackageActivityEntity(
    id = activitySummary.activityId,
    activityNumber = activitySummary.activityNumber,
    workPackage = WorkPackageEntity(id = 1L, number = 10, project = project, deactivated = false),
    translatedValues = mutableSetOf(),
    startPeriod = 1,
    endPeriod = 3,
    deliverables = mutableSetOf(),
    deactivated = false
)

val activity = WorkPackageActivity(
    id = activitySummary.activityId,
    workPackageId = 1L,
    activityNumber = 10,
    title = setOf(InputTranslation(language = SystemLanguage.EN, translation = "title")),
    description = setOf(InputTranslation(language = SystemLanguage.EN, translation = "description")),
    startPeriod = 1,
    endPeriod = 3,
    deactivated = false,
    deliverables = listOf(
        WorkPackageActivityDeliverable(
            period = 1,
            deactivated = false,
            description = setOf(InputTranslation(language = SystemLanguage.EN, translation = "description"))
        )
    )
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
            userRole = userRole,
            userStatus = UserStatus.ACTIVE
        )

        val call = CallEntity(
            id = 1,
            creator = user,
            name = "call",
            status = CallStatus.DRAFT,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now(),
            endDateStep1 = null,
            endDate = ZonedDateTime.now(),
            prioritySpecificObjectives = mutableSetOf(),
            strategies = mutableSetOf(),
            isAdditionalFundAllowed = false,
            funds = mutableSetOf(),
            lengthOfPeriod = 1,
            allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = "check-off",
            projectDefinedUnitCostAllowed = true,
            projectDefinedLumpSumAllowed = false,
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
            userStatus = UserStatus.ACTIVE
        )
    }

}
