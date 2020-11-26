package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutput
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageOutputRepository : CrudRepository<WorkPackageOutput, Long> {

    fun findTop10ByWorkPackageIdOrderByOutputNumberAsc(workPackageId: Long): Iterable<WorkPackageOutput>

    fun findFirstByWorkPackageIdAndId(workPackageId: Long, id: Long): WorkPackageOutput

    fun deleteAllByWorkPackageId(workPackageId: Long)
    
}
