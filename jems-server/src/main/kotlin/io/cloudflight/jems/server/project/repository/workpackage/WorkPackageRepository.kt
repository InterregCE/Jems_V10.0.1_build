package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageRepository: PagingAndSortingRepository<WorkPackageEntity, Long> {

    @EntityGraph(value = "WorkPackageEntity.withTranslatedValues")
    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<WorkPackageEntity>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<WorkPackageEntity>

    fun findFirstByProjectIdAndId(projectId: Long, workPackageId: Long): WorkPackageEntity

    fun countAllByProjectId(projectId: Long): Long

}
