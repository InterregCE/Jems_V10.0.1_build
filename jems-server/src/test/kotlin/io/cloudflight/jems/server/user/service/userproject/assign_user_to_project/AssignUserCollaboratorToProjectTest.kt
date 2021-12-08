package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.userproject.assign_user_collaborator_to_project.AssignUserCollaboratorToProject
import io.cloudflight.jems.server.user.service.userproject.assign_user_collaborator_to_project.MinOneManagingCollaboratorRequiredException
import io.cloudflight.jems.server.user.service.userproject.assign_user_collaborator_to_project.UsersAreNotValid
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AssignUserCollaboratorToProjectTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L

        private const val USER_ADMIN_ID = 22L
        private const val USER_PROGRAMME_ID = 23L
        private const val USER_MONITOR_ID_1 = 24L
        private const val USER_MONITOR_ID_2 = 25L
        private const val USER_APPLICANT_ID = 26L

        private const val APPLICANT_ROLE_ID = 455L
        private const val MONITOR_ROLE_ID = 456L
        private const val ADMIN_ROLE_ID = 457L
        private const val PROGRAMME_ROLE_ID = 458L

        private fun user(id: Long, email: String, roleId: Long) = UserSummary(
            id = id,
            email = email,
            name = "",
            surname = "",
            userRole = UserRoleSummary(roleId, "", false),
            userStatus = UserStatus.ACTIVE,
        )
    }

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var userRolePersistence: UserRolePersistence

    @MockK
    lateinit var collaboratorPersistence: UserProjectCollaboratorPersistence

    @InjectMockKs
    lateinit var assignUser: AssignUserCollaboratorToProject

    @Test
    fun updateUserAssignmentsOnProject() {

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns
            setOf(APPLICANT_ROLE_ID, ADMIN_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns listOf(
            user(USER_ADMIN_ID, "admin1", ADMIN_ROLE_ID),
            user(USER_APPLICANT_ID, "applicant1", APPLICANT_ROLE_ID),
        )

        val userData = slot<Map<Long, CollaboratorLevel>>()
        val expectedResult = listOf(
            CollaboratorAssignedToProject(userId = USER_ADMIN_ID, userEmail = "admin1", CollaboratorLevel.EDIT),
            CollaboratorAssignedToProject(userId = USER_APPLICANT_ID, userEmail = "applicant1", CollaboratorLevel.MANAGE),
        )
        every { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, capture(userData)) } returns expectedResult

        assertThat(assignUser.updateUserAssignmentsOnProject(PROJECT_ID, setOf(
            Pair("admin1", CollaboratorLevel.EDIT),
            Pair("applicant1", CollaboratorLevel.MANAGE),
        ))).containsExactlyElementsOf(expectedResult)

        assertThat(allEmails.captured).containsExactlyInAnyOrder("admin1", "applicant1")
        assertThat(userData.captured).containsExactlyEntriesOf(mapOf(
            USER_ADMIN_ID to CollaboratorLevel.EDIT,
            USER_APPLICANT_ID to CollaboratorLevel.MANAGE,
        ))
    }

    @Test
    fun `updateUserAssignmentsOnProject - not valid user`() {
        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns
            setOf(APPLICANT_ROLE_ID, ADMIN_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns emptyList()

        assertThrows<UsersAreNotValid> { assignUser.updateUserAssignmentsOnProject(PROJECT_ID, setOf(Pair("applicant1", CollaboratorLevel.MANAGE))) }
    }

    @Test
    fun `updateUserAssignmentsOnProject - missing manager`() {
        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns
            setOf(APPLICANT_ROLE_ID, ADMIN_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns listOf(user(USER_APPLICANT_ID, "applicant1", APPLICANT_ROLE_ID))

        assertThrows<MinOneManagingCollaboratorRequiredException> {
            assignUser.updateUserAssignmentsOnProject(PROJECT_ID, setOf(Pair("applicant1", CollaboratorLevel.VIEW)))
        }
    }

}
