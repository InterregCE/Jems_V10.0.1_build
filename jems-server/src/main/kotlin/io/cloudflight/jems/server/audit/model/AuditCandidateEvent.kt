package io.cloudflight.jems.server.audit.model

import io.cloudflight.jems.server.audit.service.AuditCandidate
import org.springframework.context.ApplicationEvent

class AuditCandidateEvent(
    context: Any,
    val auditCandidate: AuditCandidate,
    val overrideCurrentUser: AuditUser? = null
) : ApplicationEvent(context)
