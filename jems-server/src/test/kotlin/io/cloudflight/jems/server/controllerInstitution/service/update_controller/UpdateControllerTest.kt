package io.cloudflight.jems.server.controllerInstitution.service.update_controller

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionValidator
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution.UpdateController
import io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project.UsersAreNotValid
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.USER_ROLE_ID
import io.cloudflight.jems.server.utils.partner.userSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class UpdateControllerTest : UnitTest() {

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var userRolePersistence: UserRolePersistence

    @RelaxedMockK
    lateinit var userPersistence: UserPersistence

    @InjectMockKs
    lateinit var controllerInstitutionValidator : ControllerInstitutionValidator

    @InjectMockKs
    lateinit var updateController: UpdateController

    private val INSTITUTION_ID = 1L
    private val createdAt = ZonedDateTime.now()
    private val userEmail = "user@email.com"
    private val userList =
        listOf(ControllerInstitutionUser(INSTITUTION_ID, USER_ID, userEmail, UserInstitutionAccessLevel.View))
    private val institution = ControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = userList,
        createdAt = createdAt
    )

    private val institutionWithUsers = UpdateControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = userList,
        createdAt = createdAt
    )

    @Test
    fun updateInstitution() {
        every { controllerInstitutionPersistence.updateControllerInstitution(institutionWithUsers) } returns institution
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)
        every { userPersistence.findAllByEmails(any()) } returns listOf(userSummary(USER_ID, USER_ROLE_ID))
        Assertions.assertThat(updateController.updateControllerInstitution(INSTITUTION_ID, institutionWithUsers))
            .isEqualTo(institution)
    }

    @Test
    fun `updateInstitution throw invalid user exception`() {
        every { controllerInstitutionPersistence.updateControllerInstitution(institutionWithUsers) } returns institution
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)
        every { userPersistence.findAllByEmails(any()) } returns listOf(userSummary(USER_ID, 11))
        assertThrows<UsersAreNotValid> {updateController.updateControllerInstitution(INSTITUTION_ID, institutionWithUsers) }
    }
}
