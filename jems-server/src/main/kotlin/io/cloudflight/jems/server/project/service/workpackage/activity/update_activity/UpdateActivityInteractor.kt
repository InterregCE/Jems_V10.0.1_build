package io.cloudflight.jems.server.project.service.workpackage.activity.update_activity

import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity

interface UpdateActivityInteractor {

    fun updateActivitiesForWorkPackage(workPackageId: Long, activities: List<WorkPackageActivity>): List<WorkPackageActivity>

}
