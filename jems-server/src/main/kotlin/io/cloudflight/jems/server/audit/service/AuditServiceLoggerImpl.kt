package io.cloudflight.jems.server.audit.service

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

    override fun logEvent(event: AuditCandidate) {
        val user = securityService.currentUser?.user
        with(event) {
            logger.info("AUDIT >>> {} (projectId {}, user ({}, {})) : {}", action, projectId, user?.id, user?.email, description)
        }
    }

    override fun logEvent(event: AuditCandidateWithUser) {
        with(event) {
            logger.info("AUDIT >>> {} (projectId {}, user ({}, {})) : {}", action, projectId, event.user.id, event.user.email, description)
        }
    }

}
