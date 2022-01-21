package io.cloudflight.jems.server.call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallFundRateEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.FundSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

private val account = UserEntity(
    id = 1,
    email = "admin@admin.dev",
    name = "Name",
    surname = "Surname",
    userRole = UserRoleEntity(id = 1, name = "ADMIN"),
    password = "hash_pass",
    userStatus = UserStatus.ACTIVE
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
    preSubmissionCheckPluginKey = null
).apply {
    translatedValues.add(CallTranslEntity(TranslationId(this, SystemLanguage.EN), "This is a dummy call"))
    flatRates.add(
        ProjectCallFlatRateEntity(
            setupId = FlatRateSetupId(call = this, type = FlatRateType.STAFF_COSTS),
            rate = 5,
            isAdjustable = true
        )
    )
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
)

fun partnerWithId(id: Long) = ProjectPartnerEntity(
    id = id,
    project = dummyProject,
    abbreviation = "test abbr",
    role = ProjectPartnerRole.LEAD_PARTNER,
    legalStatus = ProgrammeLegalStatusEntity(),
)

fun userWithId(id: Long) = LocalCurrentUser(
    user = User(
        id = id,
        email = "x@y",
        name = "",
        surname = "",
        userRole = UserRole(0, "", permissions = emptySet(), isDefault = false),
        userStatus = UserStatus.ACTIVE
    ),
    password = "hash_pass",
    authorities = emptyList(),
)

fun callFundRate(fundId: Long) = CallFundRate(
    programmeFund = ProgrammeFund(id = fundId, selected = true),
    rate = BigDecimal.TEN,
    adjustable = true
)

fun callFundRateEntity(call: CallEntity, fundId: Long) = CallFundRateEntity(
    setupId = FundSetupId(call, ProgrammeFundEntity(id = fundId, selected = true)),
    rate = BigDecimal.TEN,
    isAdjustable = true
)

fun callFund(fundId: Long) = ProgrammeFund(
    id = fundId,
    selected = true
)

fun callDetail(
    id : Long = 10L,
    name : String = "call name",
    status : CallStatus = CallStatus.PUBLISHED,
    startDate : ZonedDateTime = ZonedDateTime.of(2020,1,10,10,10,10,10, ZoneId.systemDefault()),
    endDateStep1 : ZonedDateTime = ZonedDateTime.of(2020,1,15,10,10,10,10, ZoneId.systemDefault()),
    endDate : ZonedDateTime = ZonedDateTime.of(2020,1,30,15,10,10,10, ZoneId.systemDefault()),
    isAdditionalFundAllowed : Boolean = true,
    lengthOfPeriod : Int = 12,
    applicationFormFieldConfigurations : MutableSet<ApplicationFormFieldConfiguration> = mutableSetOf(),
    preSubmissionCheckPluginKey: String? = null
) = CallDetail(
    id = id,
    name = name,
    status = status,
    startDate = startDate,
    endDateStep1 = endDateStep1,
    endDate = endDate,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    lengthOfPeriod = lengthOfPeriod,
    applicationFormFieldConfigurations =  applicationFormFieldConfigurations,
    preSubmissionCheckPluginKey = preSubmissionCheckPluginKey
)
