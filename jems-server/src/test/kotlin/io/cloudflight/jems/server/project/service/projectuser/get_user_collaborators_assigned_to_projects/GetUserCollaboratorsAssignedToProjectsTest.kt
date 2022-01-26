package io.cloudflight.jems.server.project.service.projectuser.get_user_collaborators_assigned_to_projects

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetUserCollaboratorsAssignedToProjectsTest : UnitTest() {

    companion object {
        private val collaborator = CollaboratorAssignedToProject(userId = 400L, userEmail = "four@hundred.com", level = ProjectCollaboratorLevel.MANAGE)
    }

    @MockK
    lateinit var collaboratorPersistence: UserProjectCollaboratorPersistence

    @InjectMockKs
    lateinit var getCollaborators: GetUserCollaboratorsAssignedToProjects

    @Test
    fun getUserIdsForProject() {
        every { collaboratorPersistence.getUserIdsForProject(12L) } returns listOf(collaborator)
        assertThat(getCollaborators.getUserIdsForProject(12L)).containsExactly(collaborator)
    }

}
