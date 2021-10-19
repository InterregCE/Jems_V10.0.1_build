package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
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
        val usersToAdd = slot<Set<Long>>()
        val usersToRemove = slot<Set<Long>>()
        every { userProjectPersistence.changeUsersAssignedToProject(
            362L,
            userIdsToRemove = capture(usersToRemove),
            userIdsToAssign = capture(usersToAdd),
        ) } returns setOf(USER_WITHOUT_PROJECT_RETRIEVE_ID)

        val testResult = assignUserToProject.updateUserAssignmentsOnProject(
            362L,
            userIdsToRemove = setOf(7L, 8L),
            userIdsToAssign = setOf(
                USER_WITH_PROJECT_RETRIEVE_ID,
                USER_WITH_PROJECT_RETRIEVE_EDIT_USER_ASSIGNMENTS_ID,
                USER_WITHOUT_PROJECT_RETRIEVE_ID,
            ),
        )
        assertThat(testResult).containsExactly(USER_WITHOUT_PROJECT_RETRIEVE_ID)
        assertThat(usersToRemove.captured).containsExactlyInAnyOrder(7L, 8L)
        assertThat(usersToAdd.captured).containsExactlyInAnyOrder(USER_WITHOUT_PROJECT_RETRIEVE_ID)
    }

}
