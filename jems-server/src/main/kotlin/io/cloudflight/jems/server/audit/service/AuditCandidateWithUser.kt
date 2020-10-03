package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.entity.AuditUser

data class AuditCandidateWithUser(
    val action: AuditAction,
    val projectId: String? = null,
    val description: String,
    val user: AuditUser
) {
    fun logWithService(auditService: AuditService) {
        auditService.logEvent(this)
    }
}
