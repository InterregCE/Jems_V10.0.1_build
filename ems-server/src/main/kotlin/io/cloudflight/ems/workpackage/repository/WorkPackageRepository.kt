package io.cloudflight.ems.workpackage.repository

import io.cloudflight.ems.workpackage.entity.WorkPackage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageRepository: PagingAndSortingRepository<WorkPackage, Long> {

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<WorkPackage>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<WorkPackage>

    fun findFirstByProjectIdAndId(projectId: Long, workPackageId: Long): WorkPackage

}
