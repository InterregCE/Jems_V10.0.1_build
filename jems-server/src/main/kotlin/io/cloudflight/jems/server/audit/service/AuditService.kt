package io.cloudflight.jems.server.audit.service

interface AuditService {

    fun logEvent(event: AuditCandidate)

    fun logEvent(event: AuditCandidateWithUser)

}
