package io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborators

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator.GetPartnerUserCollaborators
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetPartnerUserCollaboratorsTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PARTNER_ID = 2L
        private const val USER_ID = 3L
    }

    @RelaxedMockK
    lateinit var collaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getCollaborators: GetPartnerUserCollaborators

    @Test
    fun `get collaborators for users with MANAGE privilege`() {
        every { userAuthorization.hasManageProjectPrivilegesPermission(PROJECT_ID) } returns true

        getCollaborators.getPartnerCollaborators(PROJECT_ID)

        verify(exactly = 1) {
            collaboratorPersistence.findPartnerCollaboratorsByProjectId(PROJECT_ID, emptySet())
        }
    }

    @Test
    fun `get collaborators for users that only see their own teams`() {
        every { userAuthorization.hasManageProjectPrivilegesPermission(PROJECT_ID) } returns false
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { collaboratorPersistence.findPartnerIdsByUserAndProject(USER_ID, PROJECT_ID) } returns setOf(PARTNER_ID)

        getCollaborators.getPartnerCollaborators(PROJECT_ID)

        verify(exactly = 1) {
            collaboratorPersistence.findPartnerCollaboratorsByProjectId(PROJECT_ID, setOf(PARTNER_ID))
        }
    }

}
