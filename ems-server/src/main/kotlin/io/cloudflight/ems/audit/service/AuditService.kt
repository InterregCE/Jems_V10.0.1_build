package io.cloudflight.ems.audit.service

interface AuditService {

    fun logEvent(event: AuditCandidate)

    fun logEvent(event: AuditCandidateWithUser)

}
