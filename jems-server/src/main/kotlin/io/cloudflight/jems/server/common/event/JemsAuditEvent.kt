package io.cloudflight.jems.server.common.event

import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditCandidate

interface JemsAuditEvent : JemsEvent {
    val auditUser: AuditUser?
    fun getAuditCandidate(): AuditCandidate
}
