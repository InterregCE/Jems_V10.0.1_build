package io.cloudflight.jems.server.project.service.workpackage.activity.update_activity

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.validateWorkPackageActivities
import io.cloudflight.jems.server.project.service.workpackage.activity.validateWorkPackageActivityPartners
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateActivity(
    private val persistence: WorkPackagePersistence,
    private val partnerPersistence: PartnerPersistence
) : UpdateActivityInteractor {

    @CanUpdateProjectWorkPackage
    @Transactional
    override fun updateActivitiesForWorkPackage(
        projectId: Long,
        workPackageId: Long,
        activities: List<WorkPackageActivity>
    ): List<WorkPackageActivity> {
        validateWorkPackageActivities(activities)
        val projectPartnerIds = partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted())
            .mapNotNull { it.id }
        validateWorkPackageActivityPartners(activities, projectPartnerIds)

        return persistence.updateWorkPackageActivities(workPackageId, activities)
    }

}
