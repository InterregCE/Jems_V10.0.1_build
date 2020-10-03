package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.server.audit.entity.AuditAction

class AuditBuilder(action: AuditAction) {

    var action: AuditAction? = action
        private set
    var projectId: String? = null
        private set
    var description: String? = null
        private set

    fun projectId(projectId: Long) = apply { this.projectId = projectId.toString() }
    fun description(description: String) = apply { this.description = description }

    fun build(): AuditCandidate {
        if (action != null && description != null)
            return AuditCandidate(action = action!!, projectId = projectId, description = description!!)
        throw UnsupportedOperationException("empty audit message")
    }

    fun logWithService(auditService: AuditService) {
        auditService.logEvent(build())
    }
}
