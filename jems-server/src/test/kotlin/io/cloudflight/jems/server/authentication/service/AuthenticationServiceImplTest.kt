package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.authentication.dto.LoginRequest
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.utils.failedLoginAttemptEntity
import io.cloudflight.jems.server.utils.loginEmail
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.Collections
import javax.servlet.http.HttpServletRequest

class AuthenticationServiceImplTest : UnitTest() {

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var loginAttemptService: LoginAttemptService

    @MockK
    lateinit var authenticationManager: AuthenticationManager

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var req: HttpServletRequest

    @InjectMockKs
    lateinit var authenticationService: AuthenticationServiceImpl

    @BeforeEach
    fun setup() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `logging in is audited`() {
        every { authenticationManager.authenticate(any()) } returns mockk()

        every { securityService.currentUser } returns LocalCurrentUser(
            User(
                1, "admin@test.net", "test", "test",
                UserRole(1, "Role", emptySet()),
                userStatus = UserStatus.ACTIVE
            ), "", Collections.emptyList()
        )
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        authenticationService.login(req, LoginRequest("admin@test.net", "admin"))

        val expectedCredentials = UsernamePasswordAuthenticationToken("admin@test.net", "admin")
        verify(exactly = 1) { authenticationManager.authenticate(expectedCredentials) }

        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.USER_LOGGED_IN,
                description = "user with email admin@test.net logged in",
            )
        )
    }

    @Test
    fun `logging out is audited`() {
        val user = mockk<User>()
        every { user.id } returns 10L
        every { user.email } returns "logging@out.user"
        every { user.userRole } returns UserRole(id = 548L, name = "role-dummy", permissions = emptySet())

        every { securityService.currentUser?.user } returns user
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }
        authenticationService.logout(req)

        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.USER_LOGGED_OUT,
                description = "user with email logging@out.user logged out",
            )
        )
        assertThat(auditSlot.captured.overrideCurrentUser).isEqualTo(AuditUser(id = 10L, email = "logging@out.user"))
    }

    @Test
    fun `current user is returned`() {
        every { securityService.currentUser } returns LocalCurrentUser(
            User(
                1, "test@test.net", "test", "test",
                UserRole(1, "Role", emptySet()),
                userStatus = UserStatus.ACTIVE
            ), "", Collections.emptyList()
        )

        val currentUser = authenticationService.getCurrentUser()

        assertThat(currentUser.name).isEqualTo("test@test.net")
        assertThat(currentUser.role.name).isEqualTo("Role")
    }

    @Test
    fun `current user is null`() {
        every { securityService.currentUser } returns null
        val currentUser = authenticationService.getCurrentUser()

        assertThat(currentUser.name).isEqualTo("")
        assertThat(currentUser.role).isEqualTo(
            UserRoleDTO(
                name = "",
                permissions = emptyList(),
                defaultForRegisteredUser = false
            )
        )
    }

    @Test
    fun `should throw LoginBlockedException when there have been many failed login attempts recently`() {
        every { loginAttemptService.getFailedLoginAttempt(loginEmail) } returns failedLoginAttemptEntity()

        assertThrows<LoginBlockedException> {
            authenticationService.login(req, LoginRequest(loginEmail, "password"))
        }

    }
}
