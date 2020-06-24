package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.LoginRequest
import io.cloudflight.ems.api.dto.OutputAccount
import io.cloudflight.ems.api.dto.OutputAccountRole
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.impl.AuthenticationServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import java.util.Collections
import javax.servlet.http.HttpServletRequest

class AuthenticationServiceTest {

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var authenticationManager: AuthenticationManager

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var req: HttpServletRequest

    @InjectMockKs
    lateinit var authenticationService: AuthenticationServiceImpl

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `logging in is audited`() {
        authenticationService.login(req, LoginRequest("admin", "admin"))

        verify {
            auditService.logEvent(
                withArg {
                    assertThat(it.description).isEqualTo("user with email admin logged in")
                })
        }
    }

    @Test
    fun `logging out is audited`() {
        authenticationService.logout(req)

        verify {
            auditService.logEvent(
                withArg {
                    assertThat(it.description).isEqualTo("user with email  logged out")
                })
        }
    }

    @Test
    fun `current user is returned`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            OutputAccount(
                1, "test@test.net", "test", "test",
                OutputAccountRole(1, "Role")
            ), "", Collections.emptyList()
        )

        val currentUser = authenticationService.getCurrentUser()

        assertThat(currentUser?.name).isEqualTo("test@test.net")
        assertThat(currentUser?.role).isEqualTo("Role")
    }

    @Test
    fun `current user is null`() {
        val currentUser = authenticationService.getCurrentUser()

        assertThat(currentUser?.name).isEqualTo("")
        assertThat(currentUser?.role).isEqualTo("")
    }
}
