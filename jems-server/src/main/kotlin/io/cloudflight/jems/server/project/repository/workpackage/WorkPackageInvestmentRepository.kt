package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageInvestmentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageInvestmentRepository : PagingAndSortingRepository<WorkPackageInvestmentEntity, Long> {

    fun findAllByWorkPackageId(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestmentEntity>

    fun findAllByWorkPackageId(workPackageId: Long, sort: Sort): Iterable<WorkPackageInvestmentEntity>

}
