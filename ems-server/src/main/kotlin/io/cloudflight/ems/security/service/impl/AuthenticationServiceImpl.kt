package io.cloudflight.ems.security.service.impl

import io.cloudflight.ems.api.dto.LoginRequest
import io.cloudflight.ems.api.dto.user.OutputCurrentUser
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.security.service.AuthenticationService
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
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
    private val auditService: AuditService
) : AuthenticationService {

    companion object {
        private val log = LoggerFactory.getLogger(AuthenticationServiceImpl::class.java)
    }

    override fun getCurrentUser(): OutputCurrentUser? {
        val user = securityService.currentUser?.user?.email ?: ""
        val role = securityService.currentUser?.user?.userRole?.name ?: ""
        return OutputCurrentUser(user, role)
    }

    override fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser? {
        log.info("Attempting login for email {}", loginRequest.email)

        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )
        val session = req.getSession(true)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext())

        auditService.logEvent(Audit.userLoggedIn(loginRequest.email))

        log.info("Logged in successfully for email {}", loginRequest.email)
        return getCurrentUser();
    }

    override fun logout(req: HttpServletRequest) {
        log.info("Logging out for current user with email {}", getCurrentUser()!!.name)

        auditService.logEvent(Audit.userLoggedOut(getCurrentUser()!!.name))

        SecurityContextHolder.clearContext();
        req.logout();
    }
}
