package io.cloudflight.ems.service

import io.cloudflight.ems.config.AUDIT_ENABLED
import io.cloudflight.ems.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.ems.entity.Audit
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
class AuditServiceLoggerImpl : AuditService {

    companion object {
        private val logger = LoggerFactory.getLogger(AuditServiceLoggerImpl::class.java)
    }

    override fun logEvent(event: Audit) {
        with(event) {
            logger.info("AUDIT >>> {} (projectId {}, user ({}, {})) : {}", action, projectId, user?.id, user?.email, description)
        }
    }

}
