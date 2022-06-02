package io.cloudflight.jems.server.project.service.workpackage.activity.update_activity

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.validateWorkPackageActivities
import io.cloudflight.jems.server.project.service.workpackage.activity.validateWorkPackageActivityConfiguration
import io.cloudflight.jems.server.project.service.workpackage.activity.validateWorkPackageActivityPartners
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateActivity(
    private val persistence: WorkPackagePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val callPersistence: CallPersistence
) : UpdateActivityInteractor {

    @CanUpdateProjectWorkPackage
    @Transactional
    @ExceptionWrapper(UpdateActivityException::class)
    override fun updateActivitiesForWorkPackage(
        projectId: Long,
        workPackageId: Long,
        activities: List<WorkPackageActivity>
    ): List<WorkPackageActivity> {
        val afConfig = callPersistence.getCallByProjectId(projectId).applicationFormFieldConfigurations
        validateWorkPackageActivityConfiguration(afConfig, activities)
        validateWorkPackageActivities(activities)
        val availablePartnerIds = partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted())
            .mapNotNullTo(HashSet()) { it.id }
        validateWorkPackageActivityPartners(activities, availablePartnerIds)

        return persistence.updateWorkPackageActivities(workPackageId, activities)
    }

}
