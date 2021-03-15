package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.jems.server.authentication.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = AUDIT_PROPERTY_PREFIX,
    name = [AUDIT_ENABLED],
    havingValue = "false",
    matchIfMissing = true // default when properties not specified
)
class AuditServiceLoggerImpl(
    private val securityService: SecurityService
) : AuditService {

    companion object {
        private val logger = LoggerFactory.getLogger(AuditServiceLoggerImpl::class.java)
    }

    override fun logEvent(audit: AuditCandidate, overrideCurrentUser: AuditUser?) {
        val user = overrideCurrentUser ?: securityService.currentUser?.toEsUser()
        with(audit) {
            logger.info("AUDIT >>> {} (projectId {}, user ({}, {})) : {}", action, project?.id, user?.id, user?.email, description)
        }
    }

}
