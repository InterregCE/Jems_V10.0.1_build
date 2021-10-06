package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AuditListener(
    private val auditService: AuditService
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun pushAuditAfterSuccessfulTransaction(event: AuditCandidateEvent) =
        with(event) {
            if (overrideCurrentUser == null)
                auditService.logEvent(audit = auditCandidate)
            else
                auditService.logEvent(audit = auditCandidate, optionalUser = overrideCurrentUser)
        }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    fun saveAuditLog(event: JemsAuditEvent) =
        if (event.auditUser == null)
            auditService.logEvent(audit = event.getAuditCandidate())
        else
            auditService.logEvent(audit = event.getAuditCandidate(), optionalUser = event.auditUser)

}
