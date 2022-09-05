package io.cloudflight.jems.server.controllerInstitution

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.nuts.entity.NutsCountry
import io.cloudflight.jems.server.nuts.entity.NutsRegion1
import io.cloudflight.jems.server.nuts.entity.NutsRegion2
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary


const val INSTITUTION_ID = 1L

const val MONITOR_USER_1_EMAIL = "monitor1@user.com"
const val MONITOR_USER_1_ID = 7L
const val MONITOR_USER_2_EMAIL = "monitor2@user.com"
const val MONITOR_USER_2_ID = 8L
const val MONITOR_USER_3_EMAIL = "monitor3@user.com"
const val MONITOR_USER_3_ID = 9L

val institutionUsers = mutableSetOf(
    ControllerInstitutionUser(
        INSTITUTION_ID,
        MONITOR_USER_1_ID,
        MONITOR_USER_1_EMAIL,
        UserInstitutionAccessLevel.View
    ),
    ControllerInstitutionUser(
        INSTITUTION_ID,
        MONITOR_USER_2_ID,
        MONITOR_USER_2_EMAIL,
        UserInstitutionAccessLevel.Edit
    )
)

val userSummaries = listOf(
    UserSummary(
        id = MONITOR_USER_1_ID,
         email = MONITOR_USER_1_EMAIL,
         name = "user1",
         surname = "",
         userRole = UserRoleSummary(4, "Controller"),
         userStatus= UserStatus.ACTIVE
    ),
    UserSummary(
        id = MONITOR_USER_2_ID,
        email = MONITOR_USER_2_EMAIL,
        name = "user2",
        surname = "",
        userRole = UserRoleSummary(4, "Controller"),
        userStatus= UserStatus.ACTIVE
    )
)

val nutsRegion3Entity = NutsRegion3(
    id = "RO113",
    title = "Cluj",
    region2 = NutsRegion2(
        id = "RO11",
        title = "Nord-Vest",
        region1 = NutsRegion1(
            id = "RO1",
            title = "Macroregiunea Unu",
            country = NutsCountry(
                id = "RO",
                title = "România"
            )
        )
    )
)

val approvedAndAfterApprovedApplicationStatuses = setOf(
    ApplicationStatus.APPROVED,
    ApplicationStatus.MODIFICATION_PRECONTRACTING,
    ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED,
    ApplicationStatus.CONTRACTED,
    ApplicationStatus.IN_MODIFICATION,
    ApplicationStatus.MODIFICATION_SUBMITTED,
    ApplicationStatus.MODIFICATION_REJECTED
)


val nutsAustria = NutsRegion3(
    "AT130",
    "Wien (AT130)",
    NutsRegion2(
        "AT13", "Wien",
        NutsRegion1(
            "AT1", "AT",
            NutsCountry("AT", "Ostösterreich")
        )
    )
)
val nutsRomania = NutsRegion3(
    "RO113",
    "Cluj (RO113)",
    NutsRegion2(
        "RO11", "Nord-Vest",
        NutsRegion1(
            "RO1", "Macroregiunea Unu",
            NutsCountry("RO", "România")
        )
    )
)

