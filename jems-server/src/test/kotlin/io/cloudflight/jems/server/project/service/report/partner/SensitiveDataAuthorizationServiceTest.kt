package io.cloudflight.jems.server.project.service.report.partner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SensitiveDataAuthorizationServiceTest: UnitTest() {

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @InjectMockKs
    lateinit var sensitiveDataAuthorizationService: SensitiveDataAuthorizationService



    @Test
    fun `partner collaborator with GDPR can view sensitive data`() {
        every { securityService.getUserIdOrThrow() } returns 9L
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingView)  } returns false
        every { partnerCollaboratorPersistence.canUserSeePartnerSensitiveData(9L, 21L) } returns true

        assertThat(sensitiveDataAuthorizationService.canViewPartnerSensitiveData(21L)).isTrue
    }

    @Test
    fun `partner collaborator with GDPR can edit sensitive data`() {
        every { securityService.getUserIdOrThrow() } returns 9L
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingView)  } returns false
        every { partnerCollaboratorPersistence.canUserSeePartnerSensitiveData(9L, 21L) } returns true

        assertThat(sensitiveDataAuthorizationService.canEditPartnerSensitiveData(21L)).isTrue
    }


    @Test
    fun `monitor user can view sensitive data`() {
        every { securityService.getUserIdOrThrow() } returns 9L
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingView)  } returns true
        every { partnerCollaboratorPersistence.canUserSeePartnerSensitiveData(9L, 21L) } returns false

        assertThat(sensitiveDataAuthorizationService.canViewPartnerSensitiveData(21L)).isTrue
    }

    @Test
    fun `monitor user can edit sensitive data`() {
        every { securityService.getUserIdOrThrow() } returns 9L
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit)  } returns true
        every { partnerCollaboratorPersistence.canUserSeePartnerSensitiveData(9L, 21L) } returns false

        assertThat(sensitiveDataAuthorizationService.canEditPartnerSensitiveData(21L)).isTrue
    }
}