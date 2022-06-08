package io.cloudflight.jems.server.project.service.partnerUser.getMyPartnerCollaboratorLevel

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

internal class GetMyPartnerCollaboratorLevelTest : UnitTest() {

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var currentUser: CurrentUser

    @InjectMockKs
    lateinit var getMyPartnerCollaboratorLevel: GetMyPartnerCollaboratorLevel

    @BeforeEach
    fun reset() {
        clearMocks(partnerCollaboratorPersistence)
        clearMocks(partnerPersistence)
        clearMocks(securityService)
        clearMocks(currentUser)
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns 17L
        every { securityService.currentUser?.hasPermission(any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(any()) } returns -1L
    }

    @Test
    fun `get my level EDIT - Role with Project Retrieve and Collaborator EDIT`() {
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectRetrieve) } returns true
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(17L, 20L) } returns Optional.of(EDIT)

        assertThat(getMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(20L))
            .isEqualTo(EDIT)
    }

    @Test
    fun `get my level EDIT - Role without anything and Collaborator EDIT`() {
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(17L, 40L) } returns Optional.of(EDIT)

        assertThat(getMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(40L))
            .isEqualTo(EDIT)
    }

    @Test
    fun `get my level EDIT - Role with EDIT and assigned project and Collaborator empty`() {
        every { partnerPersistence.getProjectIdForPartnerId(60L) } returns 360L
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) } returns true
        every { securityService.currentUser?.hasAccessToProject(360L) } returns true
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(17L, 60L) } returns Optional.empty()

        assertThat(getMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(60L))
            .isEqualTo(EDIT)
    }

    @Test
    fun `get my level VIEW - Role without anything and Collaborator VIEW`() {
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(17L, 80L) } returns Optional.of(VIEW)

        assertThat(getMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(80L))
            .isEqualTo(VIEW)
    }

    @Test
    fun `get my level VIEW - Role with VIEW and assigned project and Collaborator empty`() {
        every { partnerPersistence.getProjectIdForPartnerId(100L) } returns 400L
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingView) } returns true
        every { securityService.currentUser?.hasAccessToProject(400L) } returns true
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(17L, 100L) } returns Optional.empty()

        assertThat(getMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(100L))
            .isEqualTo(VIEW)
    }

    @Test
    fun `get my level NONE - Role without anything and Collaborator empty`() {
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(17L, 120L) } returns Optional.empty()
        assertThat(getMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(120L)).isNull()
    }

}
