package io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.projectuser.CollaboratorLevel
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetMyCollaboratorLevelTest : UnitTest() {

    @MockK
    lateinit var collaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getCollaborators: GetMyCollaboratorLevel

    @Test
    fun getUserIdsForProject() {
        every { securityService.getUserIdOrThrow() } returns 16L
        every { collaboratorPersistence.getLevelForProjectAndUser(15L, 16L) } returns CollaboratorLevel.MANAGE
        assertThat(getCollaborators.getMyCollaboratorLevel(15L)).isEqualTo(CollaboratorLevel.MANAGE)
    }

}
