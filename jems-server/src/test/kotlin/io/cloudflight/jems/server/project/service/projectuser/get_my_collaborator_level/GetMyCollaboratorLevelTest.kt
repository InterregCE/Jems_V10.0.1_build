package io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetMyCollaboratorLevelTest : UnitTest() {

    @MockK
    lateinit var projectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getCollaborators: GetMyCollaboratorLevel

    @Test
    fun getUserIdsForProject() {
        every { securityService.getUserIdOrThrow() } returns 16L
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(15L, 16L) } returns ProjectCollaboratorLevel.MANAGE
        assertThat(getCollaborators.getMyCollaboratorLevel(15L)).isEqualTo(ProjectCollaboratorLevel.MANAGE)
    }

    @Test
    fun `partner collaborators have view project level`() {
        every { securityService.getUserIdOrThrow() } returns 16L
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(15L, 16L) } returns null
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(15L) } returns setOf(16L)
        assertThat(getCollaborators.getMyCollaboratorLevel(15L)).isEqualTo(ProjectCollaboratorLevel.VIEW)
    }

}
