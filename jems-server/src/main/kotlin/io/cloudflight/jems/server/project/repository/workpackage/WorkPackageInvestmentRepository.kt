package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageInvestmentRepository : PagingAndSortingRepository<WorkPackageInvestmentEntity, Long> {

    fun findAllByWorkPackageId(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestmentEntity>

    fun findAllByWorkPackageId(workPackageId: Long, sort: Sort): Iterable<WorkPackageInvestmentEntity>

    @Query("SELECT investment.id FROM project_work_package_investment investment where investment.workPackage.project.id = :projectId")
    fun findInvestmentIds(@Param("projectId") projectId: Long): List<Long>
}
