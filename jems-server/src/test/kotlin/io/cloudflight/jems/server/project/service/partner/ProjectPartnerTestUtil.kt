package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import java.time.ZonedDateTime

class ProjectPartnerTestUtil {

    companion object {

        val userRole = UserRole(1, "ADMIN")
        val user = User(
            id = 1,
            name = "Name",
            password = "hash",
            email = "admin@admin.dev",
            surname = "Surname",
            userRole = userRole)

        val call = CallEntity(
            id = 1,
            creator = user,
            name = "call",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            priorityPolicies = emptySet(),
            strategies = emptySet(),
            funds = emptySet(),
            lengthOfPeriod = 1
        )
        val projectStatus = ProjectStatus(
            status = ProjectApplicationStatus.APPROVED,
            user = user,
            updated = ZonedDateTime.now())
        val project = Project(
            id = 1,
            acronym = "acronym",
            call = call,
            applicant = user,
            projectStatus = projectStatus)
    }

}
