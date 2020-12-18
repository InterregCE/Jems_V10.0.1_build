package io.cloudflight.jems.server.project.service.workpackage.activity.update_activity

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.validateWorkPackageActivities
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateActivity(
    private val persistence: WorkPackagePersistence
) : UpdateActivityInteractor {

    @CanUpdateProjectWorkPackage
    @Transactional
    override fun updateActivitiesForWorkPackage(
        workPackageId: Long,
        activities: List<WorkPackageActivity>
    ): List<WorkPackageActivity> {
        validateWorkPackageActivities(activities)
        return persistence.updateWorkPackageActivities(workPackageId, activities)
    }

}
