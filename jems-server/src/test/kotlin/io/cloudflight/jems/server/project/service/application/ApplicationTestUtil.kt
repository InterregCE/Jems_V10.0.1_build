package io.cloudflight.jems.server.project.service.application

import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.ZonedDateTime

val callSettings = ProjectCallSettings(
    callId = 2L,
    callName = "call",
    startDate = ZonedDateTime.now(),
    endDate = ZonedDateTime.now(),
    lengthOfPeriod = 2,
    endDateStep1 = null,
    isAdditionalFundAllowed = false,
    flatRates = emptySet(),
    lumpSums = emptyList(),
    unitCosts = emptyList(),
    stateAids = emptyList(),
    applicationFormFieldConfigurations = mutableSetOf()
)

fun projectWithId(id: Long, status: ApplicationStatus = ApplicationStatus.SUBMITTED) = ProjectFull(
    id = id,
    customIdentifier = "01",
    callSettings = callSettings,
    acronym = "project acronym",
    applicant = UserSummary(3L, "email", "name", "surname", UserRoleSummary(4L, "role")),
    projectStatus = ProjectStatus(
        id = null,
        status = status,
        user = UserSummary(0, "", "", "", UserRoleSummary(name = "")),
        updated = ZonedDateTime.now(),
    ),
    duration = 10,
)
