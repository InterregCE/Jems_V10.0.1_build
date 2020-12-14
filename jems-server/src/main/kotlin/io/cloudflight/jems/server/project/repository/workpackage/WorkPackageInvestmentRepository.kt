package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageInvestmentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WorkPackageInvestmentRepository : PagingAndSortingRepository<WorkPackageInvestmentEntity, UUID> {

    fun findAllByWorkPackageId(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestmentEntity>

}
