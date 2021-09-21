package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectSummary

class AuditBuilder(action: AuditAction) {

    var action: AuditAction? = action
        private set
    var project: AuditProject? = null
        private set
    var entityRelatedId: Long? = null
        private set
    var description: String? = null
        private set

    fun project(project: ProjectSummary) = apply { this.project = AuditProject(id = project.id.toString(), customIdentifier = project.customIdentifier, name = project.acronym) }
    fun project(project: ProjectDetail) = apply { this.project = AuditProject(id = project.id.toString(), customIdentifier = project.customIdentifier, name = project.acronym) }
    fun entityRelatedId(entityRelatedId: Long) = apply { this.entityRelatedId = entityRelatedId }
    fun description(description: String) = apply { this.description = description }

    fun build(): AuditCandidate {
        if (action != null && description != null)
            return AuditCandidate(action = action!!, project = project, entityRelatedId = entityRelatedId, description = description!!)
        throw UnsupportedOperationException("empty audit message")
    }

}
