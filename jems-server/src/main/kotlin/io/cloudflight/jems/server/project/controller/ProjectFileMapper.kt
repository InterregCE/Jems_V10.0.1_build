package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryDTO
import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryTypeDTO
import io.cloudflight.jems.api.project.dto.file.ProjectFileMetadataDTO
import io.cloudflight.jems.server.common.CommonDTOMapper
import io.cloudflight.jems.server.project.controller.report.sizeToString
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.user.controller.toDto
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun ProjectFileCategoryTypeDTO.toModel() = projectFileMapper.map(this)
fun ProjectFileCategoryDTO.toModel() = projectFileMapper.map(this)
fun ProjectFileMetadata.toDTO() = projectFileMapper.map(this)
fun Page<ProjectFileMetadata>.toDTO() = map { projectFileMapper.map(it) }

private val projectFileMapper = Mappers.getMapper(ProjectFileMapper::class.java)

@Mapper(uses = [CommonDTOMapper::class])
abstract class ProjectFileMapper {
    abstract fun map(fileCategoryTypDTO: ProjectFileCategoryTypeDTO): ProjectFileCategoryType
    abstract fun map(fileCategoryDTO: ProjectFileCategoryDTO): ProjectFileCategory

    fun map(fileMetadata: ProjectFileMetadata): ProjectFileMetadataDTO =
        ProjectFileMetadataDTO(
            fileMetadata.id,
            fileMetadata.projectId,
            fileMetadata.name,
            fileMetadata.size,
            fileMetadata.size.sizeToString(),
            fileMetadata.uploadedAt,
            fileMetadata.uploadedBy.toDto(),
            fileMetadata.description
        )
}

