package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectSummary

fun projectFileUploadSuccess(
    context: Any,
    fileMeta: JemsFileMetadata,
    location: String,
    type: JemsFileType,
    projectSummary: ProjectSummary,
): AuditCandidateEvent = fileUploadSuccess(
    context = context,
    id = fileMeta.id,
    type = type.name,
    name = fileMeta.name,
    location = location,
    projectSummary = projectSummary,
)

@Deprecated("New file structure should be used.")
fun projectFileUploadSuccessOld(
    context: Any,
    fileMeta: ProjectFileMetadata,
    location: String,
    projectFileCategory: ProjectFileCategory,
    projectSummary: ProjectSummary,
): AuditCandidateEvent = fileUploadSuccess(
    context = context,
    id = fileMeta.id,
    type = projectFileCategory.type.name,
    name = fileMeta.name,
    location = location,
    projectSummary = projectSummary,
)

private fun fileUploadSuccess(
    context: Any,
    id: Long,
    type: String,
    name: String,
    location: String,
    projectSummary: ProjectSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY)
            .project(projectSummary)
            .entityRelatedId(id)
            .description("File (of type $type) \"$name\" has been uploaded to $location")
            .build()
    )

fun fileDescriptionChanged(
    context: Any,
    fileMeta: JemsFileMetadata,
    location: String,
    oldValue: String,
    newValue: String,
    projectSummary: ProjectSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED)
            .project(projectSummary)
            .entityRelatedId(fileMeta.id)
            .description("Description of file \"${fileMeta.name}\" uploaded to $location has changed from \"$oldValue\" to \"$newValue\"")
            .build()
    )

fun fileDeleted(
    context: Any,
    fileId: Long,
    location: String,
    projectSummary: ProjectSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_FILE_DELETED)
            .project(projectSummary)
            .entityRelatedId(fileId)
            .description(location)
            .build()
    )

fun fileDeleted(
    context: Any,
    fileId: Long,
    location: String,
    project: ProjectFull,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_FILE_DELETED)
            .project(project)
            .entityRelatedId(fileId)
            .description(location)
            .build()
    )

