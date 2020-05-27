package io.cloudflight.ems.service

import io.cloudflight.ems.config.AUDIT_ENABLED
import io.cloudflight.ems.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.repository.AuditRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnProperty(
    prefix = AUDIT_PROPERTY_PREFIX,
    name = [AUDIT_ENABLED],
    havingValue = "true"
)
class AuditServiceImpl(
    private val auditRepository: AuditRepository
) : AuditService {

    @Transactional
    override fun logEvent(event: Audit) {
        auditRepository.save(event)
    }

}
