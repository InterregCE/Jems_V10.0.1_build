package io.cloudflight.jems.server.security.service.impl

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.entity.AuditUser
import io.cloudflight.jems.server.audit.service.AuditCandidateWithUser
import io.cloudflight.jems.server.security.model.LocalCurrentUser
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.audit.service.toEsUser
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

@Service
class HttpSessionService(private val auditService: AuditService) : HttpSessionListener {
    companion object {
        private val log = LoggerFactory.getLogger(HttpSessionService::class.java)
    }

    override fun sessionCreated(event: HttpSessionEvent) {
        log.info("Http session created for {}", event.session.getAttribute(SPRING_SECURITY_CONTEXT_KEY));
    }

    override fun sessionDestroyed(event: HttpSessionEvent) {
        log.info("Http session destroyed for {}", event.session.getAttribute(SPRING_SECURITY_CONTEXT_KEY));

        val session: Any? = event.session.getAttribute(SPRING_SECURITY_CONTEXT_KEY) ?: return
        (session as SecurityContextImpl)
            .authentication?.let { authentication ->
                auditService.logEvent(
                    userSessionExpiredAudit(
                        (authentication.principal as LocalCurrentUser).toEsUser()
                    )
                )
            }
    }

    private fun userSessionExpiredAudit(user: AuditUser): AuditCandidateWithUser =
        AuditCandidateWithUser(
            action = AuditAction.USER_SESSION_EXPIRED,
            user = user,
            description = "user with email ${user.email} was logged out by the system"
        )

}
