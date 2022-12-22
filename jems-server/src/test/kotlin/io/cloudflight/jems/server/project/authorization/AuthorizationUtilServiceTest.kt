package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorizationUtilServiceTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 9L
        private const val PARTNER_COLLABORATOR_USER_ID = 7L
        private const val PROGRAMME_USER_ID = 8L
        private const val CONTROLLER_USER_ID = 2L

        private val partnerCollaborator = PartnerCollaborator(
            userId = PARTNER_COLLABORATOR_USER_ID,
            partnerId = PARTNER_COLLABORATOR_USER_ID,
            userEmail = "user05@jems.eu",
            level = PartnerCollaboratorLevel.EDIT
        )
    }

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence
    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var authorizationUtilService: AuthorizationUtilService

    @Test
    fun userIsPartnerCollaboratorForProject() {
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = any(),
            projectId = any(),
        ) } returns setOf(partnerCollaborator)

        assertThat(authorizationUtilService.userIsPartnerCollaboratorForProject(PARTNER_COLLABORATOR_USER_ID, PROJECT_ID)).isTrue
    }

    @Test
    fun userIsNotPartnerCollaboratorForProject() {
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = any(),
            projectId = any(),
        ) } returns emptySet()
        assertThat(authorizationUtilService.userIsPartnerCollaboratorForProject(PROGRAMME_USER_ID, PROJECT_ID)).isFalse
    }

    @Test
    fun userIsControllerForProject() {
        every {
            controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID)
        } returns setOf(CONTROLLER_USER_ID)
        val currentUser: CurrentUser = mockk()
        every { currentUser.user.id } returns CONTROLLER_USER_ID
        every { currentUser.hasPermission(UserRolePermission.ProjectMonitorCollaboratorsRetrieve) } returns true
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns CONTROLLER_USER_ID

        assertThat(authorizationUtilService
            .hasPermissionAsController(UserRolePermission.ProjectMonitorCollaboratorsRetrieve, PROJECT_ID)).isTrue
    }

    @Test
    fun userIsNotControllerForProject() {
        every {
            controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID)
        } returns setOf(CONTROLLER_USER_ID)
        val currentUser: CurrentUser = mockk()
        every { currentUser.user.id } returns 3L
        every { currentUser.hasPermission(UserRolePermission.ProjectMonitorCollaboratorsRetrieve) } returns true
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 3L

        assertThat(authorizationUtilService
            .hasPermissionAsController(UserRolePermission.ProjectMonitorCollaboratorsRetrieve, PROJECT_ID)).isFalse
    }

}
