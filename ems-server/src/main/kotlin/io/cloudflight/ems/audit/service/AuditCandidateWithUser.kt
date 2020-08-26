package io.cloudflight.ems.audit.service

import io.cloudflight.ems.audit.entity.AuditAction
import io.cloudflight.ems.audit.entity.AuditUser

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
