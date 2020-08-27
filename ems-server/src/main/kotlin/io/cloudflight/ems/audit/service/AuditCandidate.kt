package io.cloudflight.ems.audit.service

import io.cloudflight.ems.audit.entity.AuditAction

data class AuditCandidate(
    val action: AuditAction,
    val projectId: String? = null,
    val description: String
) {
    fun logWithService(auditService: AuditService) {
        auditService.logEvent(this)
    }
}
