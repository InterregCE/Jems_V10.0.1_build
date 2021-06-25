package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.ZonedDateTime

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
            lengthOfPeriod = 1,
            applicationFormFieldConfigurationEntities = mutableSetOf()
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
