package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
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

    override fun logEvent(audit: AuditCandidate, optionalUser: AuditUser?) {
        val actualUser = if(securityService.currentUser!=null) securityService.currentUser?.toEsUser() else optionalUser
        with(audit) {
            logger.info("AUDIT >>> {} (projectId {}, user ({}, {})) : {}", action, project?.id, actualUser?.id, actualUser?.email, description)
        }
    }

}
