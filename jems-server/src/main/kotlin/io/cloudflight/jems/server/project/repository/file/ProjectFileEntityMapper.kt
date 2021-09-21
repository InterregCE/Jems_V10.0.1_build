package io.cloudflight.jems.server.project.repository.file

import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFileEntity
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import org.springframework.data.domain.Page


fun Page<ProjectFileEntity>.toModel(): Page<ProjectFileMetadata> = this.map { it.toModel() }
fun ProjectFileEntity.toModel() =
    ProjectFileMetadata(id, project.id, name, size, updated, user.toUserSummary(), description)

fun List<ProjectFileCategoryEntity>.toFileCategoryTypeSet() =
    filter { !it.categoryId.type.contains("=") }
        .mapTo(mutableSetOf()) { ProjectFileCategoryType.valueOf(it.categoryId.type) }
        .also { it.add(ProjectFileCategoryType.ALL) }.toSet()
