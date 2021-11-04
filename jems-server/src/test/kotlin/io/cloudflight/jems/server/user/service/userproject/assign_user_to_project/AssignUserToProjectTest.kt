package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.model.UpdateProjectUser
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class AssignUserToProjectTest : UnitTest() {

    companion object {
        private const val USER_WITH_PROJECT_RETRIEVE_ID = 17L
        private const val USER_WITH_PROJECT_RETRIEVE_EDIT_USER_ASSIGNMENTS_ID = 18L
        private const val USER_WITHOUT_PROJECT_RETRIEVE_ID = 19L

        val project = ProjectSummary(1L, "cid", "call", "acronym", ApplicationStatus.STEP1_DRAFT)
        val userRole = UserRoleSummary(2L, "role", false)
        val user = UserSummary(USER_WITHOUT_PROJECT_RETRIEVE_ID, "email", "", "", userRole, UserStatus.ACTIVE)
    }

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @MockK
    lateinit var userWithProjectRetrieve: UserWithPassword

    @MockK
    lateinit var userWithProjectRetrieveEditUserAssignments: UserWithPassword

    @MockK
    lateinit var userWithoutProjectRetrieve: UserWithPassword

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var assignUserToProject: AssignUserToProject

    @BeforeEach
    fun setup() {
        every { userWithProjectRetrieve.userRole } returns UserRole(name = "", permissions = setOf(
            ProjectRetrieve,
        ))
        every { userWithProjectRetrieveEditUserAssignments.userRole } returns UserRole(name = "", permissions = setOf(
            ProjectRetrieve,
            ProjectRetrieveEditUserAssignments,
        ))
        every { userWithoutProjectRetrieve.userRole } returns UserRole(name = "", permissions = emptySet())

        every { userPersistence.getById(USER_WITH_PROJECT_RETRIEVE_ID) } returns userWithProjectRetrieve
        every { userPersistence.getById(USER_WITH_PROJECT_RETRIEVE_EDIT_USER_ASSIGNMENTS_ID) } returns userWithProjectRetrieveEditUserAssignments
        every { userPersistence.getById(USER_WITHOUT_PROJECT_RETRIEVE_ID) } returns userWithoutProjectRetrieve
    }

    @Test
    fun updateUserAssignmentsOnProject() {
        val projectIdsSlot = mutableListOf<Long>()
        val removeUsersSlot = mutableListOf<Set<Long>>()
        val addUsersSlot = mutableListOf<Set<Long>>()
        val capturedAudits = mutableListOf<AssignUserEvent>()

        val project1 = ProjectSummary(362L, "cid362", "call", "acronym1", ApplicationStatus.STEP1_DRAFT)
        val project2 = ProjectSummary(363L, "cid363", "call", "acronym2", ApplicationStatus.STEP1_DRAFT)
        every { projectPersistence.getProjectSummary(362L) } returns project1
        every { projectPersistence.getProjectSummary(363L) } returns project2
        every { userPersistence.findAllByIds(setOf(USER_WITHOUT_PROJECT_RETRIEVE_ID)) } returns listOf(user)

        every { userProjectPersistence.changeUsersAssignedToProject(
            capture(projectIdsSlot),
            userIdsToRemove = capture(removeUsersSlot),
            userIdsToAssign = capture(addUsersSlot),
        ) } returns setOf(USER_WITHOUT_PROJECT_RETRIEVE_ID)

        assignUserToProject.updateUserAssignmentsOnProject(setOf(
            // test available users filtering
            UpdateProjectUser(
                362L,
                userIdsToRemove = setOf(7L, 8L),
                userIdsToAdd = setOf(
                    USER_WITH_PROJECT_RETRIEVE_ID,
                    USER_WITH_PROJECT_RETRIEVE_EDIT_USER_ASSIGNMENTS_ID,
                    USER_WITHOUT_PROJECT_RETRIEVE_ID,
                ),
            ),
            // test another persist call is called
            UpdateProjectUser(
                363L,
                userIdsToRemove = setOf(99L),
                userIdsToAdd = emptySet(),
            ),
            // test that this is not called to be persisted
            UpdateProjectUser(
                364L,
                userIdsToRemove = emptySet(),
                userIdsToAdd = emptySet(),
            ),
        ))

        verify(exactly = 2) { userProjectPersistence.changeUsersAssignedToProject(any(), any(), any()) }
        verify(exactly = 2) { eventPublisher.publishEvent(capture(capturedAudits)) }
        assertThat(capturedAudits).containsExactly(
            AssignUserEvent(project1, listOf(user)),
            AssignUserEvent(project2, listOf(user))
        )

        assertThat(projectIdsSlot[0]).isEqualTo(362L)
        assertThat(removeUsersSlot[0]).containsExactlyInAnyOrder(7L, 8L)
        assertThat(addUsersSlot[0]).containsExactly(USER_WITHOUT_PROJECT_RETRIEVE_ID)

        assertThat(projectIdsSlot[1]).isEqualTo(363L)
        assertThat(removeUsersSlot[1]).containsExactly(99L)
        assertThat(addUsersSlot[1]).isEmpty()
    }

}
