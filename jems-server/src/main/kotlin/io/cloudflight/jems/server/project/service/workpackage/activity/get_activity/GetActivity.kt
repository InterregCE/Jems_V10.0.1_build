package io.cloudflight.jems.server.project.service.workpackage.activity.get_activity

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetActivity(
    private val persistence: WorkPackagePersistence
) : GetActivityInteractor {

    @CanRetrieveProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getActivitiesForWorkPackage(workPackageId: Long): List<WorkPackageActivity> =
        persistence.getWorkPackageActivitiesForWorkPackage(workPackageId)

}
