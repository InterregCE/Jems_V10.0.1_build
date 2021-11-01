package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.UpdateProjectUserDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.ProjectWithUsers
import io.cloudflight.jems.server.user.service.model.UpdateProjectUser
import io.cloudflight.jems.server.user.service.userproject.assign_user_to_project.AssignUserToProjectInteractor
import io.cloudflight.jems.server.user.service.userproject.get_users_assigned_to_projects.GetUsersAssignedToProjectsInteractor
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
        private const val PROJECT_ID = 1489L
        private val PAGE = Pageable.unpaged()

        private val dummyProjectUserDto = ProjectUserDTO(
            id = PROJECT_ID,
            customIdentifier = "FGR45_001",
            acronym = "P1 G4",
            projectStatus = ApplicationStatusDTO.DRAFT,
            assignedUserIds = setOf(998L, 999L),
        )

        private val dummyProjectUser = ProjectWithUsers(
            id = PROJECT_ID,
            customIdentifier = "FGR45_001",
            acronym = "P1 G4",
            projectStatus = ApplicationStatus.DRAFT,
            assignedUserIds = setOf(998L, 999L),
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
        every { getUsersAssignedToProjectsInteractor.getProjectsWithAssignedUsers(PAGE) } returns
            PageImpl(listOf(dummyProjectUser))
        assertThat(controller.listProjectsWithAssignedUsers(PAGE).content).containsExactly(dummyProjectUserDto)
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
