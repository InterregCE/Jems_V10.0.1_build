package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageActivityRepository: PagingAndSortingRepository<WorkPackageActivityEntity, Long> {

    @EntityGraph(value = "WorkPackageActivityEntity.full")
    fun findAllByActivityIdWorkPackageIdIn(workPackageIds: Collection<Long>): Iterable<WorkPackageActivityEntity>

}
