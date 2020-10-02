package io.cloudflight.ems.nuts.authorization

import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.programmeUser
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NutsAuthorizationTest {

    @MockK
    lateinit var securityService: SecurityService

    lateinit var nutsAuthorization: NutsAuthorization

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        nutsAuthorization = NutsAuthorization(securityService)
    }

    @Test
    fun `admin user can download nuts`() {
        val user = adminUser
        every { securityService.currentUser } returns user

        assertTrue(
            nutsAuthorization.canSetupNuts(),
            "${user.user.email} should be able to access nuts setup"
        )
    }

    @Test
    fun `applicant user cannot download nuts`() {
        val user = applicantUser
        every { securityService.currentUser } returns user

        assertFalse(
            nutsAuthorization.canSetupNuts(),
            "${user.user.email} should NOT be able to access nuts setup"
        )
    }

    @Test
    fun `programme user can download nuts`() {
        val user = programmeUser
        every { securityService.currentUser } returns user

        assertTrue(
            nutsAuthorization.canSetupNuts(),
            "${user.user.email} should be able to access nuts setup"
        )
    }

}
