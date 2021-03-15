package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditProject

class AuditBuilder(action: AuditAction) {

    var action: AuditAction? = action
        private set
    var project: AuditProject? = null
        private set
    var entityRelatedId: Long? = null
        private set
    var description: String? = null
        private set

    fun project(id: Long, name: String? = null) = apply { this.project = AuditProject(id = id.toString(), name = name) }
    fun entityRelatedId(entityRelatedId: Long) = apply { this.entityRelatedId = entityRelatedId }
    fun description(description: String) = apply { this.description = description }

    fun build(): AuditCandidate {
        if (action != null && description != null)
            return AuditCandidate(action = action!!, project = project, entityRelatedId = entityRelatedId, description = description!!)
        throw UnsupportedOperationException("empty audit message")
    }

}
