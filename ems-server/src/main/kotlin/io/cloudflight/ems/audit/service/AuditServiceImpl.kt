package io.cloudflight.ems.audit.service

import io.cloudflight.ems.config.AUDIT_ENABLED
import io.cloudflight.ems.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.ems.audit.entity.Audit
import io.cloudflight.ems.audit.repository.AuditRepository
import io.cloudflight.ems.security.service.SecurityService
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
    private val securityService: SecurityService,
    private val auditRepository: AuditRepository
) : AuditService {

    @Transactional
    override fun logEvent(event: AuditCandidate) {
        auditRepository.save(
            Audit(
                action = event.action,
                projectId = event.projectId,
                user = securityService.currentUser?.toEsUser(),
                description = event.description
            )
        )
    }

    @Transactional
    override fun logEvent(event: AuditCandidateWithUser) {
        auditRepository.save(
            Audit(
                action = event.action,
                projectId = event.projectId,
                user = event.user,
                description = event.description
            )
        )
    }

}
