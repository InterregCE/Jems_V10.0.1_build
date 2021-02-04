package io.cloudflight.jems.server.call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import java.time.ZonedDateTime

private val account = User(
    id = 1,
    email = "admin@admin.dev",
    name = "Name",
    surname = "Surname",
    userRole = UserRole(id = 1, name = "ADMIN"),
    password = "hash_pass"
)

val testUser = OutputUserWithRole(
    id = 1,
    email = "admin@admin.dev",
    name = "Name",
    surname = "Surname",
    userRole = OutputUserRole(id = 1, name = "ADMIN")
)

private val testCall = CallEntity(
    id = 0,
    creator = account,
    name = "Test call name",
    prioritySpecificObjectives = emptySet(),
    strategies = emptySet(),
    multipleFundsAllowed = false,
    funds = emptySet(),
    startDate = ZonedDateTime.now(),
    endDate = ZonedDateTime.now().plusDays(5L),
    status = CallStatus.DRAFT,
    lengthOfPeriod = 1,
    description = "This is a dummy call",
    flatRates = mutableSetOf(ProjectCallFlatRateEntity(
        setupId = FlatRateSetupId(callId = 0, type = FlatRateType.STAFF_COSTS),
        rate = 5,
        isAdjustable = true
    ))
)

fun callWithId(id: Long) = testCall.copy(id = id)

private val dummyProject = ProjectEntity(
    id = 1,
    call = testCall,
    acronym = "Test Project",
    applicant = testCall.creator,
    projectStatus = ProjectStatus(id = 1, status = ProjectApplicationStatus.DRAFT, user = testCall.creator)
)

fun partnerWithId(id: Long) = ProjectPartnerEntity(
    id = id,
    project = dummyProject,
    abbreviation = "test abbr",
    role = ProjectPartnerRole.LEAD_PARTNER,
    legalStatus = ProgrammeLegalStatus()
)
