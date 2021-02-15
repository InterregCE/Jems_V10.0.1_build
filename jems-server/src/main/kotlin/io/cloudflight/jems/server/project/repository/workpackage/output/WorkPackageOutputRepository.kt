package io.cloudflight.jems.server.project.repository.workpackage.output

import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageOutputRepository: PagingAndSortingRepository<WorkPackageOutputEntity, Long> {

    @EntityGraph(value = "WorkPackageOutputEntity.full")
    fun findAllByOutputIdWorkPackageIdIn(workPackageIds: Collection<Long>): Iterable<WorkPackageOutputEntity>

}
