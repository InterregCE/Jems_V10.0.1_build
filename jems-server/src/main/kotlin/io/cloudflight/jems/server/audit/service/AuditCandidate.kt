package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditProject

data class AuditCandidate(
    val action: AuditAction,
    val project: AuditProject? = null,
    val entityRelatedId: Long? = null,
    val description: String
) {

    @Deprecated("Instantiate AuditCandidate and pass it through ApplicationEventPublisher", replaceWith = ReplaceWith("publishEvent"))
    fun logWith(auditService: AuditService) {
        auditService.logEvent(audit = this)
    }
}
