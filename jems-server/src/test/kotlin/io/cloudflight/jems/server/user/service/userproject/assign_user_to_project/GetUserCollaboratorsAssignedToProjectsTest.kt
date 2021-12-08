package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.entity.CollaboratorLevel
import io.cloudflight.jems.server.user.service.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.userproject.get_user_collaborators_assigned_to_projects.GetUserCollaboratorsAssignedToProjects
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetUserCollaboratorsAssignedToProjectsTest : UnitTest() {

    companion object {
        private val collaborator = CollaboratorAssignedToProject(userId = 400L, userEmail = "four@hundred.com", level = CollaboratorLevel.MANAGE)
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
