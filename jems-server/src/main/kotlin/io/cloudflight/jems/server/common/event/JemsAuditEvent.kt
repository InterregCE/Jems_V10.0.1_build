package io.cloudflight.jems.server.common.event

import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditCandidate

data class JemsAuditEvent(
    val auditUser: AuditUser? =  null,
    val auditCandidate: AuditCandidate
) : JemsEvent
