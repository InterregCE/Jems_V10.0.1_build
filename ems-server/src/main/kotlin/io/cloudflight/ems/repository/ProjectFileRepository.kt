package io.cloudflight.ems.repository

import io.cloudflight.ems.api.dto.ProjectFileType
import io.cloudflight.ems.entity.ProjectFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectFileRepository : PagingAndSortingRepository<ProjectFile, Long> {

    fun findAllByProjectIdAndType(projectId: Long, type: ProjectFileType, pageable: Pageable): Page<ProjectFile>

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectFile>

    fun findFirstByProjectIdAndNameAndType(projectId: Long, name: String, type: ProjectFileType): Optional<ProjectFile>

}
