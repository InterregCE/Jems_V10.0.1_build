package io.cloudflight.jems.server.project.service.workpackage.activity.get_activity

import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity

interface GetActivityInteractor {

    fun getActivitiesForWorkPackage(workPackageId: Long, version: String? = null): List<WorkPackageActivity>

}
