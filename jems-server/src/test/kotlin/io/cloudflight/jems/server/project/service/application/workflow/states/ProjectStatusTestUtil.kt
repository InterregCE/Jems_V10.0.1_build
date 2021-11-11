package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectStatusTestUtil {

    companion object {

        fun getStatusModelForStatus(status: ApplicationStatus): ProjectStatus {
            return ProjectStatus(
                status = status,
                user = ProjectPartnerTestUtil.userSummary,
                updated = ZonedDateTime.now(),
                decisionDate = LocalDate.of(2021, 5, 19),
                entryIntoForceDate = LocalDate.of(2021, 5, 19),
                note = "example note",
            )
        }

        val userSummary = UserSummary(
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRoleSummary(name = "role"),
            userStatus = UserStatus.ACTIVE
        )
    }

}
