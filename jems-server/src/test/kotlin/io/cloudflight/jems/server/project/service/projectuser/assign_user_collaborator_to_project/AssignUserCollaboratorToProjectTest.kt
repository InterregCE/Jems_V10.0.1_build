package io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class AssignUserCollaboratorToProjectTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L

        private const val USER_ADMIN_ID = 22L
        private const val USER_APPLICANT_ID = 26L

        private const val APPLICANT_ROLE_ID = 455L
        private const val ADMIN_ROLE_ID = 457L

        private fun user(id: Long, email: String, roleId: Long) = UserSummary(
            id = id,
            email = email,
            name = "",
            surname = "",
            userRole = UserRoleSummary(roleId, "", false),
            userStatus = UserStatus.ACTIVE,
        )

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.DRAFT,
        )
    }

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var userRolePersistence: UserRolePersistence

    @MockK
    lateinit var collaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var assignUser: AssignUserCollaboratorToProject

    @BeforeEach
    fun resetAuditService() {
        clearMocks(eventPublisher)
    }

    @Test
    fun updateUserAssignmentsOnProject() {

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns
            setOf(APPLICANT_ROLE_ID, ADMIN_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns listOf(
            user(USER_ADMIN_ID, "admin1", ADMIN_ROLE_ID),
            user(USER_APPLICANT_ID, "applicant1", APPLICANT_ROLE_ID),
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        val userData = slot<Map<Long, ProjectCollaboratorLevel>>()
        val expectedResult = listOf(
            CollaboratorAssignedToProject(userId = USER_ADMIN_ID, userEmail = "admin1", ProjectCollaboratorLevel.EDIT),
            CollaboratorAssignedToProject(userId = USER_APPLICANT_ID, userEmail = "applicant1", ProjectCollaboratorLevel.MANAGE),
        )
        every { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, capture(userData)) } returns expectedResult

        assertThat(assignUser.updateUserAssignmentsOnProject(
            PROJECT_ID, setOf(
            Pair("admin1", ProjectCollaboratorLevel.EDIT),
            Pair("applicant1", ProjectCollaboratorLevel.MANAGE),
        ))).containsExactlyElementsOf(expectedResult)

        assertThat(allEmails.captured).containsExactlyInAnyOrder("admin1", "applicant1")
        assertThat(userData.captured).containsExactlyEntriesOf(mapOf(
            USER_ADMIN_ID to ProjectCollaboratorLevel.EDIT,
            USER_APPLICANT_ID to ProjectCollaboratorLevel.MANAGE,
        ))

        verify(exactly = 1) { eventPublisher.publishEvent(AssignUserCollaboratorEvent(
            project = projectSummary,
            collaborators = expectedResult,
        )) }
    }

    @Test
    fun `updateUserAssignmentsOnProject - not valid user`() {
        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns
            setOf(APPLICANT_ROLE_ID, ADMIN_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns emptyList()

        assertThrows<UsersAreNotValid> { assignUser.updateUserAssignmentsOnProject(PROJECT_ID, setOf(Pair("applicant1", ProjectCollaboratorLevel.MANAGE))) }
    }

    @Test
    fun `updateUserAssignmentsOnProject - missing manager`() {
        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(setOf(ProjectCreate), emptySet()) } returns
            setOf(APPLICANT_ROLE_ID, ADMIN_ROLE_ID)

        val allEmails = slot<Collection<String>>()
        every { userPersistence.findAllByEmails(capture(allEmails)) } returns listOf(user(USER_APPLICANT_ID, "applicant1", APPLICANT_ROLE_ID))

        assertThrows<MinOneManagingCollaboratorRequiredException> {
            assignUser.updateUserAssignmentsOnProject(PROJECT_ID, setOf(Pair("applicant1", ProjectCollaboratorLevel.VIEW)))
        }
    }

}
