package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel

class AuditBuilder(action: AuditAction) {

    var action: AuditAction? = action
        private set
    var project: AuditProject? = null
        private set
    var entityRelatedId: Long? = null
        private set
    var description: String? = null
        private set

    fun project(project: ProjectSummary) = apply {
        this.project = AuditProject(
            id = project.id.toString(),
            customIdentifier = project.customIdentifier,
            name = project.acronym
        )
    }

    fun project(project: ProjectDetail) = apply {
        this.project = AuditProject(
            id = project.id.toString(),
            customIdentifier = project.customIdentifier,
            name = project.acronym
        )
    }

    fun project(project: ProjectFull) = apply {
        this.project = AuditProject(
            id = project.id.toString(),
            customIdentifier = project.customIdentifier,
            name = project.acronym
        )
    }

    fun project(projectId: Long, customIdentifier: String, acronym: String) = apply {
        this.project = AuditProject(
            id = projectId.toString(),
            customIdentifier = customIdentifier,
            name = acronym,
        )
    }

    fun project(report: ProjectReportModel) = apply {
        this.project = AuditProject(
            id = report.projectId.toString(),
            customIdentifier = report.projectIdentifier,
            name = report.projectAcronym,
        )
    }

    fun entityRelatedId(entityRelatedId: Long) = apply { this.entityRelatedId = entityRelatedId }
    fun description(description: String) = apply { this.description = description }

    fun build(): AuditCandidate {
        if (action != null && description != null)
            return AuditCandidate(action = action!!, project = project, entityRelatedId = entityRelatedId, description = description!!)
        throw UnsupportedOperationException("empty audit message")
    }

}
