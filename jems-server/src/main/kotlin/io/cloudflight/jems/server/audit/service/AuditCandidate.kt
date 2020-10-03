package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.entity.AuditAction

data class AuditCandidate(
    val action: AuditAction,
    val projectId: String? = null,
    val description: String
) {
    fun logWith(auditService: AuditService) {
        auditService.logEvent(this)
    }
}
