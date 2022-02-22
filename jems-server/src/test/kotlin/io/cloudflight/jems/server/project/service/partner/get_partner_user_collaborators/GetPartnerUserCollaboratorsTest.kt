package io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborators

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator.GetPartnerUserCollaborators
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetPartnerUserCollaboratorsTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PARTNER_ID = 2L
        private const val USER_ID = 3L

        val partnerCollaborator = PartnerCollaborator(
            userId = USER_ID,
            partnerId = PARTNER_ID,
            userEmail = "test",
            level = PartnerCollaboratorLevel.EDIT
        )
    }

    @MockK
    lateinit var collaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getCollaborators: GetPartnerUserCollaborators

    @BeforeEach
    fun reset() {
        clearMocks(userAuthorization, collaboratorPersistence)
    }

    @Test
    fun `get collaborators for users with MANAGE privilege`() {
        every { userAuthorization.hasManageProjectPrivilegesPermission(PROJECT_ID) } returns true
        every { collaboratorPersistence.findPartnerCollaboratorsByProjectId(PROJECT_ID) } returns setOf(partnerCollaborator)

        assertThat(getCollaborators.getPartnerCollaborators(PROJECT_ID))
            .containsExactly(partnerCollaborator)

        verify(exactly = 1) {
            userAuthorization.hasManageProjectPrivilegesPermission(PROJECT_ID)
            collaboratorPersistence.findPartnerCollaboratorsByProjectId(PROJECT_ID)
        }
    }

    @Test
    fun `get collaborators for users that only see their own teams`() {
        every { userAuthorization.hasManageProjectPrivilegesPermission(PROJECT_ID) } returns false
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { collaboratorPersistence.findPartnersByUserAndProject(USER_ID, PROJECT_ID) } returns setOf(partnerCollaborator)
        every { collaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(PARTNER_ID)) } returns setOf(partnerCollaborator)

        assertThat(getCollaborators.getPartnerCollaborators(PROJECT_ID))
            .containsExactly(partnerCollaborator)

        verify(exactly = 1) {
            userAuthorization.hasManageProjectPrivilegesPermission(PROJECT_ID)
            securityService.getUserIdOrThrow()
            collaboratorPersistence.findPartnersByUserAndProject(USER_ID, PROJECT_ID)
            collaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(PARTNER_ID))
        }
    }

}
