package io.cloudflight.jems.server.call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import java.time.ZonedDateTime

private val account = UserEntity(
    id = 1,
    email = "admin@admin.dev",
    name = "Name",
    surname = "Surname",
    userRole = UserRoleEntity(id = 1, name = "ADMIN"),
    password = "hash_pass"
)

private fun testCall(id: Long = 0) = CallEntity(
    id = id,
    creator = account,
    name = "Test call name",
    status = CallStatus.DRAFT,
    startDate = ZonedDateTime.now(),
    endDateStep1 = null,
    endDate = ZonedDateTime.now().plusDays(5L),
    lengthOfPeriod = 1,
    isAdditionalFundAllowed = false,
    translatedValues = mutableSetOf(),
    prioritySpecificObjectives = mutableSetOf(),
    strategies = mutableSetOf(),
    funds = mutableSetOf(),
    flatRates = mutableSetOf(),
).apply {
    translatedValues.add(CallTranslEntity(TranslationId(this, SystemLanguage.EN),"This is a dummy call"))
    flatRates.add(ProjectCallFlatRateEntity(setupId = FlatRateSetupId(call = this, type = FlatRateType.STAFF_COSTS), rate = 5, isAdjustable = true))
}

fun callWithId(id: Long): CallEntity {
    val call = testCall(id)
    call.translatedValues.add(CallTranslEntity(TranslationId(call, SystemLanguage.EN), "This is a dummy call"))
    return call
}

private val dummyProject = ProjectEntity(
    id = 1,
    call = testCall(),
    acronym = "Test Project",
    applicant = account,
    currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
    step2Active = false
)

fun partnerWithId(id: Long) = ProjectPartnerEntity(
    id = id,
    project = dummyProject,
    abbreviation = "test abbr",
    role = ProjectPartnerRole.LEAD_PARTNER,
    legalStatus = ProgrammeLegalStatusEntity()
)

fun userWithId(id: Long) = LocalCurrentUser(
    user = User(id = id, email = "x@y", name = "", surname = "", userRole = UserRole(0, "", permissions = emptySet())),
    password = "hash_pass",
    authorities = emptyList(),
)
