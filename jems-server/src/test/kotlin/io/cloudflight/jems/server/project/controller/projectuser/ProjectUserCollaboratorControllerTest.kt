package io.cloudflight.jems.server.project.controller.projectuser

import io.cloudflight.jems.api.project.dto.assignment.ProjectCollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserCollaboratorDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project.AssignUserCollaboratorToProjectInteractor
import io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level.GetMyCollaboratorLevelInteractor
import io.cloudflight.jems.server.project.service.projectuser.get_user_collaborators_assigned_to_projects.GetUserCollaboratorsAssignedToProjectsInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectUserCollaboratorControllerTest : UnitTest() {

    @MockK
    lateinit var assignUserCollaboratorToProject: AssignUserCollaboratorToProjectInteractor

    @MockK
    lateinit var getUserCollaboratorsAssignedToProjects: GetUserCollaboratorsAssignedToProjectsInteractor

    @MockK
    lateinit var getMyCollaboratorLevel: GetMyCollaboratorLevelInteractor

    @InjectMockKs
    lateinit var controller: ProjectUserCollaboratorController

    @BeforeEach
    fun resetMocks() {
        clearMocks(assignUserCollaboratorToProject)
    }

    @Test
    fun listProjectsWithAssignedUsers() {
        every { getUserCollaboratorsAssignedToProjects.getUserIdsForProject(14L) } returns listOf(
            CollaboratorAssignedToProject(
                userId = 10L,
                userEmail = "email",
                level = ProjectCollaboratorLevel.VIEW,
            )
        )
        assertThat(controller.listAssignedUserCollaborators(14L)).containsExactly(
            ProjectUserCollaboratorDTO(
                userId = 10L,
                userEmail = "email",
                level = ProjectCollaboratorLevelDTO.VIEW,
            )
        )
    }

    @Test
    fun assignUserToProjectInteractor() {
        val dataSlot = slot<Set<Pair<String, ProjectCollaboratorLevel>>>()
        every { assignUserCollaboratorToProject.updateUserAssignmentsOnProject(60L, capture(dataSlot)) } returns emptyList()

        controller.updateAssignedUserCollaborators(
            projectId = 60L,
            users = setOf(
                UpdateProjectUserCollaboratorDTO(
                    userEmail = "email",
                    level = ProjectCollaboratorLevelDTO.MANAGE,
                ),
            ),
        )

        assertThat(dataSlot.captured).containsExactly(
            Pair("email", ProjectCollaboratorLevel.MANAGE)
        )
    }

    @Test
    fun `checkUserPermissions - existing`() {
        every { getMyCollaboratorLevel.getMyCollaboratorLevel(45L) } returns ProjectCollaboratorLevel.MANAGE
        assertThat(controller.checkMyProjectLevel(45L)).isEqualTo(ProjectCollaboratorLevelDTO.MANAGE)
    }

    @Test
    fun `checkUserPermissions - not-existing`() {
        every { getMyCollaboratorLevel.getMyCollaboratorLevel(-1L) } returns null
        assertThat(controller.checkMyProjectLevel(-1L)).isNull()
    }

}
