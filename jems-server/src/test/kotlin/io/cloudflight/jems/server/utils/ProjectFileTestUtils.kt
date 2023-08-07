package io.cloudflight.jems.server.utils

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryDTO
import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryTypeDTO
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryId
import io.cloudflight.jems.server.project.entity.file.ProjectFileEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.ZonedDateTime

const val PROJECT_ID = 1L
const val FILE_ID = 2L
const val FILE_NAME = "test.txt"
const val FILE_SIZE = 100L
const val USER_ID = 3L
const val PARTNER_ID = 4L
const val INVESTMENT_ID = 5L
const val USER_EMAIL = "user@applicant.dev"
const val USER_NAME = "applicant"
const val USER_SURNAME = "surname"
const val USER_ROLE_ID = 6L
const val USER_ROLE_NAME = "applicant user"

val fileByteArray = ByteArray(FILE_SIZE.toInt())
val projectFile = ProjectFile(fileByteArray.inputStream(), FILE_NAME, FILE_SIZE)
val file = MockMultipartFile(FILE_NAME, FILE_NAME, "text/csv", fileByteArray.inputStream())

val uploadedBy =
    UserSummary(
        USER_ID,
        USER_EMAIL,
        sendNotificationsToEmail = false,
        USER_NAME,
        USER_SURNAME,
        UserRoleSummary(USER_ROLE_ID, USER_ROLE_NAME),
        userStatus = UserStatus.ACTIVE
    )

val user = User(
    id = USER_ID,
    email = USER_EMAIL,
    userSettings = UserSettings(sendNotificationsToEmail = false),
    name = USER_NAME,
    surname = USER_SURNAME,
    userRole = UserRole(id = USER_ROLE_ID, name = USER_ROLE_NAME, permissions = emptySet(), isDefault = true),
    userStatus = UserStatus.ACTIVE
)
val currentUser = LocalCurrentUser(
    user, "hash_pass", listOf(
        SimpleGrantedAuthority("ROLE_" + user.userRole.name)
    )
)

val userRole = UserRoleEntity(USER_ROLE_ID, USER_ROLE_NAME)

val userEntity = UserEntity(
    id = USER_ID,
    name = USER_NAME,
    password = "hash",
    email = USER_EMAIL,
    sendNotificationsToEmail = false,
    surname = USER_SURNAME,
    userRole = userRole,
    userStatus = UserStatus.ACTIVE
)

val callEntity = CallEntity(
    id = 1,
    creator = userEntity,
    name = "call",
    status = CallStatus.DRAFT,
    type = CallType.STANDARD,
    startDate = ZonedDateTime.now(),
    endDateStep1 = null,
    endDate = ZonedDateTime.now(),
    prioritySpecificObjectives = mutableSetOf(),
    strategies = mutableSetOf(),
    isAdditionalFundAllowed = false,
    isDirectContributionsAllowed = true,
    funds = mutableSetOf(),
    lengthOfPeriod = 1,
    allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
    preSubmissionCheckPluginKey = null,
    firstStepPreSubmissionCheckPluginKey = null,
    reportPartnerCheckPluginKey = "check-off",
    reportProjectCheckPluginKey = "check-off",
    projectDefinedUnitCostAllowed = true,
    projectDefinedLumpSumAllowed = false,
    controlReportPartnerCheckPluginKey = "control-report-partner-check-off",
    controlReportSamplingCheckPluginKey = "control-report-sampling-check-off"
)
val projectStatusEntity = ProjectStatusHistoryEntity(
    status = ApplicationStatus.APPROVED,
    user = userEntity,
    updated = ZonedDateTime.now()
)
val projectEntity = ProjectEntity(
    id = PROJECT_ID,
    acronym = "acronym",
    call = callEntity,
    applicant = userEntity,
    currentStatus = projectStatusEntity,
)

fun projectFileEntity(Id: Long = 0L, uploaded: ZonedDateTime = ZonedDateTime.now()) =
    ProjectFileEntity(Id, FILE_NAME, projectEntity, userEntity, "", FILE_SIZE, uploaded)

fun projectFileCategoryEntity(
    fileId: Long = FILE_ID,
    categoryTypeString: String = ProjectFileCategoryType.ASSESSMENT.name,
    projectFileEntity: ProjectFileEntity
) = ProjectFileCategoryEntity(ProjectFileCategoryId(fileId, categoryTypeString), projectFileEntity)

fun projectFileCategory(
    categoryType: ProjectFileCategoryType = ProjectFileCategoryType.PARTNER, categoryId: Long? = PARTNER_ID
) = ProjectFileCategory(categoryType, categoryId)

fun fileMetadata(uploadedAt: ZonedDateTime = ZonedDateTime.now(), description: String = "") =
    ProjectFileMetadata(FILE_ID, PROJECT_ID, FILE_NAME, FILE_SIZE, uploadedAt, uploadedBy, description)

fun projectFileCategoryDTO(
    categoryTypeDTO: ProjectFileCategoryTypeDTO = ProjectFileCategoryTypeDTO.PARTNER,
    categoryId: Long? = PARTNER_ID
) = ProjectFileCategoryDTO(categoryTypeDTO, categoryId)



