package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.model.AuditUser

interface AuditService {

    fun logEvent(audit: AuditCandidate, optionalUser: AuditUser? = null)

}
