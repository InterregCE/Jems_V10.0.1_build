package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.authentication.dto.LoginRequest
import io.cloudflight.jems.api.authentication.dto.OutputCurrentUser
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.toEsUser
import io.cloudflight.jems.server.user.controller.toDto
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.servlet.http.HttpServletRequest


@Service
class AuthenticationServiceImpl(
    private val securityService: SecurityService,
    private val loginAttemptService: LoginAttemptService,
    private val authenticationManager: AuthenticationManager,
    private val auditPublisher: ApplicationEventPublisher,
) : AuthenticationService {

    companion object {
        private val log = LoggerFactory.getLogger(AuthenticationServiceImpl::class.java)
        const val MAX_ALLOWED_NUMBER_OF_LOGIN_ATTEMPTS = 10.toShort()
        const val LOGIN_BLOCK_DURATION_IN_SECONDS = 5 * 60
    }

    override fun getCurrentUser(): OutputCurrentUser {
        val id = securityService.currentUser?.user?.id ?: -1
        val user = securityService.currentUser?.user?.email ?: ""
        val role = securityService.currentUser?.user?.userRole?.toDto()
            ?: UserRoleDTO(name = "", permissions = emptyList(), defaultForRegisteredUser = false)
        return OutputCurrentUser(id, user, role)
    }

    @Transactional // can't be made readOnly because of 'authenticationManager.authenticate()'
    override fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser {
        log.info("Attempting login for email {}", loginRequest.email)

        loginAttemptService.getFailedLoginAttempt(loginRequest.email)?.let { failedLoginAttempt ->
            throwIfLoginIsBlocked(loginRequest.email, failedLoginAttempt.count, failedLoginAttempt.lastAttemptAt)
        }

        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )
        val session = req.getSession(true)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext())

        val currentUser = getCurrentUser()
        auditPublisher.publishEvent(userLoggedInAudit(this, user = currentUser.toEsUser()))

        log.info("Logged in successfully for email {}", loginRequest.email)
        return currentUser
    }

    @Transactional(readOnly = true)
    override fun logout(req: HttpServletRequest) {
        val esUser = getCurrentUser().toEsUser()
        log.info("Logging out for current user with email {}", esUser.email)

        SecurityContextHolder.clearContext()
        req.logout()

        auditPublisher.publishEvent(userLoggedOutAudit(this, esUser))
    }

    private fun userLoggedInAudit(context: Any, user: AuditUser) =
        AuditCandidateEvent(
            context = context,
            auditCandidate = AuditBuilder(AuditAction.USER_LOGGED_IN)
                .description("user with email ${user.email} logged in")
                .build(),
        )

    private fun userLoggedOutAudit(context: Any, user: AuditUser) =
        AuditCandidateEvent(
            context = context,
            auditCandidate = AuditBuilder(AuditAction.USER_LOGGED_OUT)
                .description("user with email ${user.email} logged out")
                .build(),
            overrideCurrentUser = user,
        )

    private fun throwIfLoginIsBlocked(email: String, failedLoginAttemptsCount: Short, lastAttempt: Instant) {
        val passedSecondsFromLastAttempt = lastAttempt.until(Instant.now(), ChronoUnit.SECONDS)
        if (failedLoginAttemptsCount >= MAX_ALLOWED_NUMBER_OF_LOGIN_ATTEMPTS && passedSecondsFromLastAttempt < LOGIN_BLOCK_DURATION_IN_SECONDS)
            throw LoginBlockedException(
                email, MAX_ALLOWED_NUMBER_OF_LOGIN_ATTEMPTS,
                Duration.ofSeconds(LOGIN_BLOCK_DURATION_IN_SECONDS.minus(passedSecondsFromLastAttempt)).toMinutes()
            )
    }
}
