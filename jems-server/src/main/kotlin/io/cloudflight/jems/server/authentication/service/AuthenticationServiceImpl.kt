package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.api.authentication.dto.LoginRequest
import io.cloudflight.jems.api.authentication.dto.OutputCurrentUser
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.audit.service.toEsUser
import io.cloudflight.jems.server.user.controller.toDto
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest


@Service
class AuthenticationServiceImpl(
    private val securityService: SecurityService,
    private val authenticationManager: AuthenticationManager,
    private val auditService: AuditService,
) : AuthenticationService {

    companion object {
        private val log = LoggerFactory.getLogger(AuthenticationServiceImpl::class.java)
    }

    override fun getCurrentUser(): OutputCurrentUser {
        val id = securityService.currentUser?.user?.id ?: -1
        val user = securityService.currentUser?.user?.email ?: ""
        val role = securityService.currentUser?.user?.userRole?.toDto()
            ?: UserRoleDTO(name = "", permissions = emptyList(), defaultForRegisteredUser = false)
        return OutputCurrentUser(id, user, role)
    }

    override fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser {
        log.info("Attempting login for email {}", loginRequest.email)

        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )
        val session = req.getSession(true)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext())

        val esUser = getCurrentUser().toEsUser()
        auditService.logEvent(userLoggedInAudit(esUser), esUser)

        log.info("Logged in successfully for email {}", loginRequest.email)
        return getCurrentUser()
    }

    override fun logout(req: HttpServletRequest) {
        log.info("Logging out for current user with email {}", getCurrentUser().name)

        val esUser = getCurrentUser().toEsUser()
        auditService.logEvent(userLoggedOutAudit(esUser), esUser)

        SecurityContextHolder.clearContext()
        req.logout()
    }

    private fun userLoggedInAudit(user: AuditUser) =
        AuditCandidate(
            action = AuditAction.USER_LOGGED_IN,
            description = "user with email ${user.email} logged in"
        )

    private fun userLoggedOutAudit(user: AuditUser) =
        AuditCandidate(
            action = AuditAction.USER_LOGGED_OUT,
            description = "user with email ${user.email} logged out"
        )
}
