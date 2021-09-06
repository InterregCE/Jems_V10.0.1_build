package io.cloudflight.jems.server.project.service.workpackage.activity.get_activity

import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary

interface GetActivityInteractor {

    fun getActivitiesForWorkPackage(projectId: Long, workPackageId: Long, version: String? = null): List<WorkPackageActivity>

    fun getActivitiesForProject(projectId: Long, version: String? = null): List<WorkPackageActivitySummary>

}
