package io.cloudflight.jems.server.project.controller.projectuser

import io.cloudflight.jems.api.project.dto.assignment.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.user.dto.UserRoleSummaryDTO
import io.cloudflight.jems.api.user.dto.UserStatusDTO
import io.cloudflight.jems.api.user.dto.UserSummaryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.projectuser.assign_user_to_project.AssignUserToProjectInteractor
import io.cloudflight.jems.server.project.service.projectuser.get_users_assigned_to_projects.GetUsersAssignedToProjectsInteractor
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.ProjectWithUsers
import io.cloudflight.jems.server.user.service.model.assignment.UpdateProjectUser
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ProjectUserControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = "1489"
        private val PAGE = Pageable.unpaged()

        private fun dummyUser(id: Long) = UserSummary(
            id = id,
            email = "$id-email",
            name = "$id-name",
            surname = "$id-surname",
            userRole = UserRoleSummary(
                id = 5411L,
                name = "role name",
            ),
            userStatus = UserStatus.ACTIVE,
        )

        private fun dummyUserDto(id: Long) = UserSummaryDTO(
            id = id,
            email = "$id-email",
            name = "$id-name",
            surname = "$id-surname",
            userRole = UserRoleSummaryDTO(
                id = 5411L,
                name = "role name",
                defaultForRegisteredUser = false,
            ),
            userStatus = UserStatusDTO.ACTIVE,
        )

        private val dummyProjectUserDto = ProjectUserDTO(
            id = PROJECT_ID,
            customIdentifier = "FGR45_001",
            acronym = "P1 G4",
            projectStatus = ApplicationStatusDTO.DRAFT,
            relatedCall = "1",
            users = setOf(dummyUserDto(998L), dummyUserDto(999L)),
        )

        private val dummyProjectUser = ProjectWithUsers(
            id = PROJECT_ID,
            customIdentifier = "FGR45_001",
            acronym = "P1 G4",
            projectStatus = ApplicationStatus.DRAFT,
            relatedCall = "1",
            users = setOf(dummyUser(998L), dummyUser(999L)),
        )
    }

    @MockK
    lateinit var assignUserToProjectInteractor: AssignUserToProjectInteractor

    @MockK
    lateinit var getUsersAssignedToProjectsInteractor: GetUsersAssignedToProjectsInteractor

    @InjectMockKs
    lateinit var controller: ProjectUserController

    @BeforeEach
    fun resetMocks() {
        clearMocks(assignUserToProjectInteractor)
    }

    @Test
    fun listProjectsWithAssignedUsers() {
        every { getUsersAssignedToProjectsInteractor.getProjectsWithAssignedUsers(PAGE, null) } returns
            PageImpl(listOf(dummyProjectUser))
        assertThat(controller.listProjectsWithAssignedUsers(PAGE, null).content).containsExactly(dummyProjectUserDto)
    }

    @Test
    fun assignUserToProjectInteractor() {
        val dataSlot = slot<Set<UpdateProjectUser>>()
        every { assignUserToProjectInteractor.updateUserAssignmentsOnProject(capture(dataSlot)) } answers { }

        controller.updateProjectUserAssignments(projectUsers = setOf(
            UpdateProjectUserDTO(
                projectId = 255L,
                userIdsToRemove = setOf(12L, 13L),
                userIdsToAdd = emptySet(),
            ),
            UpdateProjectUserDTO(
                projectId = 256L,
                userIdsToRemove = emptySet(),
                userIdsToAdd = setOf(24L, 25L),
            ),
        ))

        assertThat(dataSlot.captured).containsExactly(
            UpdateProjectUser(
                projectId = 255L,
                userIdsToRemove = setOf(12L, 13L),
                userIdsToAdd = emptySet(),
            ),
            UpdateProjectUser(
                projectId = 256L,
                userIdsToRemove = emptySet(),
                userIdsToAdd = setOf(24L, 25L),
            ),
        )
    }
}
