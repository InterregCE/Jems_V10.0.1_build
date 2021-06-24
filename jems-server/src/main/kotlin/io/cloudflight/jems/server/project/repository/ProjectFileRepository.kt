package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.project.entity.file.ProjectFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectFileRepository : PagingAndSortingRepository<ProjectFile, Long> {

    fun findAllByProjectIdAndType(projectId: Long, type: ProjectFileType, pageable: Pageable): Page<ProjectFile>

    fun findFirstByProjectIdAndIdAndType(projectId: Long, id: Long, type: ProjectFileType): Optional<ProjectFile>

    fun findFirstByProjectIdAndNameAndType(projectId: Long, name: String, type: ProjectFileType): Optional<ProjectFile>

}
