package io.cloudflight.jems.server.project.service.workpackage.activity.get_activity

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetActivity(
    private val persistence: WorkPackagePersistence
) : GetActivityInteractor {

    @CanRetrieveProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getActivitiesForWorkPackage(projectId: Long, workPackageId: Long, version: String?): List<WorkPackageActivity> =
        persistence.getWorkPackageActivitiesForWorkPackage(workPackageId, projectId, version)

    @CanRetrieveProjectForm
    @Transactional(readOnly = true)
    override fun getActivitiesForProject(projectId: Long, version: String?): List<WorkPackageActivitySummary> =
        persistence.getWorkPackageActivitiesForProject(projectId, version)

}
