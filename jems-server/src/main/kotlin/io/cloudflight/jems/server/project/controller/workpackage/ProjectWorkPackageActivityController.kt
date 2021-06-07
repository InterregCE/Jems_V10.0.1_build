package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageActivityApi
import io.cloudflight.jems.server.project.service.workpackage.activity.get_activity.GetActivityInteractor
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.UpdateActivityInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectWorkPackageActivityController(
    private val getActivityInteractor: GetActivityInteractor,
    private val updateActivityInteractor: UpdateActivityInteractor,
) : ProjectWorkPackageActivityApi {

    override fun getActivities(workPackageId: Long, version: String?): List<WorkPackageActivityDTO> =
        getActivityInteractor.getActivitiesForWorkPackage(workPackageId, version).toDto()

    override fun updateActivities(
        workPackageId: Long,
        activities: List<WorkPackageActivityDTO>
    ): List<WorkPackageActivityDTO> =
        updateActivityInteractor.updateActivitiesForWorkPackage(workPackageId, activities.toModel()).toDto()

}
