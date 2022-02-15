package io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class GetMyCollaboratorLevelTest : UnitTest() {

    companion object {
        val monitorUser = LocalCurrentUser(
            AuthorizationUtil.userAdmin, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.ProjectMonitorCollaboratorsUpdate.name))
        )
        val monitorRestrictedUser = LocalCurrentUser(
            AuthorizationUtil.userAdmin, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.ProjectMonitorCollaboratorsRetrieve.name))
        )
    }

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
        val projectId = 15L
        every { securityService.currentUser } returns applicantUser
        every { securityService.getUserIdOrThrow() } returns 16L
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(projectId, 16L) } returns ProjectCollaboratorLevel.MANAGE
        assertThat(getCollaborators.getMyCollaboratorLevel(projectId)).isEqualTo(ProjectCollaboratorLevel.MANAGE)
    }

    @Test
    fun `partner collaborators have view project level`() {
        val projectId = 15L
        every { securityService.currentUser } returns applicantUser
        every { securityService.getUserIdOrThrow() } returns 16L
        every { projectCollaboratorPersistence.getLevelForProjectAndUser(projectId, 16L) } returns null
        every { partnerCollaboratorPersistence.findUserIdsByProjectId(projectId) } returns setOf(16L)
        assertThat(getCollaborators.getMyCollaboratorLevel(projectId)).isEqualTo(ProjectCollaboratorLevel.VIEW)
    }

    @Test
    fun `partner monitoring user has manage project level`() {
        val projectId = 1L
        every { securityService.currentUser } returns monitorUser
        assertThat(getCollaborators.getMyCollaboratorLevel(projectId)).isEqualTo(ProjectCollaboratorLevel.MANAGE)
    }

    @Test
    fun `partner monitoring user with view permission has view project level`() {
        val projectId = 1L
        every { securityService.currentUser } returns monitorRestrictedUser
        assertThat(getCollaborators.getMyCollaboratorLevel(projectId)).isEqualTo(ProjectCollaboratorLevel.VIEW)
    }
}
