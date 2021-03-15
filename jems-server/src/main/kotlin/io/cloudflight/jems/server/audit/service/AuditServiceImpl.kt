package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = AUDIT_PROPERTY_PREFIX,
    name = [AUDIT_ENABLED],
    havingValue = "true"
)
class AuditServiceImpl(
    private val securityService: SecurityService,
    private val auditPersistence: AuditPersistence,
) : AuditService {

    companion object {
        private val logger = LoggerFactory.getLogger(AuditServiceImpl::class.java)
    }

    override fun logEvent(audit: AuditCandidate, overrideCurrentUser: AuditUser?) {
        val auditId = auditPersistence.saveAudit(
            Audit(
                action = audit.action,
                project = audit.project,
                user = overrideCurrentUser ?: securityService.currentUser?.toEsUser(),
                description = audit.description
            )
        )
        logger.info("Audit event with id=$auditId persisted to ES.")
    }

}
