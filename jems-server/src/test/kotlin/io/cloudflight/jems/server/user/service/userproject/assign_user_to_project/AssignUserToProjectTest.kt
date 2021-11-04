package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.model.UpdateProjectUser
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AssignUserToProjectTest : UnitTest() {

    companion object {
        private const val USER_WITH_PROJECT_RETRIEVE_ID = 17L
        private const val USER_WITH_PROJECT_RETRIEVE_EDIT_USER_ASSIGNMENTS_ID = 18L
        private const val USER_WITHOUT_PROJECT_RETRIEVE_ID = 19L
    }

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @MockK
    lateinit var userWithProjectRetrieve: UserWithPassword

    @MockK
    lateinit var userWithProjectRetrieveEditUserAssignments: UserWithPassword

    @MockK
    lateinit var userWithoutProjectRetrieve: UserWithPassword

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

        assertThat(projectIdsSlot[0]).isEqualTo(362L)
        assertThat(removeUsersSlot[0]).containsExactlyInAnyOrder(7L, 8L)
        assertThat(addUsersSlot[0]).containsExactly(USER_WITHOUT_PROJECT_RETRIEVE_ID)

        assertThat(projectIdsSlot[1]).isEqualTo(363L)
        assertThat(removeUsersSlot[1]).containsExactly(99L)
        assertThat(addUsersSlot[1]).isEmpty()
    }

}
