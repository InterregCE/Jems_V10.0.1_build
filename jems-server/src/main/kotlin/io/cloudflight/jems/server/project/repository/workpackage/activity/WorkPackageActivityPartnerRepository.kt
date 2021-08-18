package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageActivityPartnerRepository : JpaRepository<WorkPackageActivityPartnerEntity, WorkPackageActivityPartnerId> {

    fun findAllByIdWorkPackageActivityIdWorkPackageId(workPackageId: Long): MutableList<WorkPackageActivityPartnerEntity>

    fun deleteAllByIdWorkPackageActivityIdWorkPackageId(workPackageId: Long)

}
