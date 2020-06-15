package io.cloudflight.ems.security.service.impl

import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.service.AuditService
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
                    Audit.userSessionExpired(
                        (authentication.principal as LocalCurrentUser).username
                    )
                )
            }
    }
}
