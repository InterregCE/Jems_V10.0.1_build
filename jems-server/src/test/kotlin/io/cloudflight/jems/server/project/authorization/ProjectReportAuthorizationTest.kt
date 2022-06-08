package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingView
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class ProjectReportAuthorizationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 288L
        private const val PROJECT_ID = 305L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var currentUser: CurrentUser

    @InjectMockKs
    lateinit var reportAuthorization: ProjectReportAuthorization

    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, null) } returns PROJECT_ID
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService)
        every { securityService.currentUser } returns currentUser
    }

    @Test
    fun `assigned monitor user with permission can edit`() {
        every { currentUser.hasPermission(ProjectReportingEdit) } returns true
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        assertThat(reportAuthorization.canEditPartnerReport(PARTNER_ID)).isTrue
    }

    @Test
    fun `creator + collaborator with permission can edit`() {
        every { currentUser.hasPermission(ProjectReportingEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns 4590L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 4590L, PARTNER_ID) } returns Optional.of(EDIT)
        assertThat(reportAuthorization.canEditPartnerReport(PARTNER_ID)).isTrue
    }

    @Test
    fun `user can NOT edit`() {
        every { currentUser.hasPermission(ProjectReportingEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns 3205L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 3205L, PARTNER_ID) } returns Optional.empty()
        assertThat(reportAuthorization.canEditPartnerReport(PARTNER_ID)).isFalse
    }

    @Test
    fun `assigned monitor user with permission can view`() {
        every { currentUser.hasPermission(ProjectReportingView) } returns true
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        assertThat(reportAuthorization.canViewPartnerReport(PARTNER_ID)).isTrue
    }

    @Test
    fun `creator + collaborator with permission can view`() {
        every { currentUser.hasPermission(ProjectReportingView) } returns false
        every { securityService.getUserIdOrThrow() } returns 4590L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 4590L, PARTNER_ID) } returns Optional.of(VIEW)
        assertThat(reportAuthorization.canViewPartnerReport(PARTNER_ID)).isTrue
    }

    @Test
    fun `user can NOT view`() {
        every { currentUser.hasPermission(ProjectReportingView) } returns false
        every { securityService.getUserIdOrThrow() } returns 3205L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 3205L, PARTNER_ID) } returns Optional.empty()
        assertThat(reportAuthorization.canViewPartnerReport(PARTNER_ID)).isFalse
    }

}
