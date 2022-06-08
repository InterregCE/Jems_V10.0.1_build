package io.cloudflight.jems.server.project.service.workpackage.activity

import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.PartnersNotFound

const val ACTIVITY_MAX_ERROR_KEY = "workPackage.activity.max.allowed.reached"
const val ACTIVITY_START_PERIOD_LATE_ERROR_KEY = "workPackage.activity.startPeriod.is.after.endPeriod"
const val ACTIVITY_TITLE_SIZE_ERROR_KEY = "workPackage.activity.title.size.too.long"
const val ACTIVITY_DESCRIPTION_SIZE_ERROR_KEY = "workPackage.activity.description.size.too.long"
const val DELIVERABLE_DESCRIPTION_LONG_ERROR_KEY = "workPackage.activity.deliverable.description.size.too.long"
const val DELIVERABLES_MAX_ERROR_KEY = "workPackage.activity.deliverables.max.allowed.reached"
const val DELIVERABLES_NOT_ENABLED_ERROR_KEY = "workPackage.activity.deliverables.not.enabled"
private const val MAX_ALLOWED_ACTIVITIES = 20
private const val MAX_ALLOWED_DELIVERABLES = 20

fun validateWorkPackageActivities(workPackageActivities: Collection<WorkPackageActivity>) {
    if (workPackageActivities.size > MAX_ALLOWED_ACTIVITIES)
        throw I18nValidationException(i18nKey = ACTIVITY_MAX_ERROR_KEY)
    if (!workPackageActivities.all { it.deliverables.size <= MAX_ALLOWED_DELIVERABLES })
        throw I18nValidationException(i18nKey = DELIVERABLES_MAX_ERROR_KEY)

    val isStartAlwaysBeforeEnd = workPackageActivities
        .filter { it.startPeriod != null && it.endPeriod != null }
        .all { it.startPeriod!! <= it.endPeriod!! }
    if (!isStartAlwaysBeforeEnd)
        throw I18nValidationException(i18nKey = ACTIVITY_START_PERIOD_LATE_ERROR_KEY)

    if (!workPackageActivities.all { it.title.all { (it.translation?.length ?: 0) <= 200 } })
        throw I18nValidationException(i18nKey = ACTIVITY_TITLE_SIZE_ERROR_KEY)
    if (!workPackageActivities.all { it.description.all { (it.translation?.length ?: 0) <= 1000 } })
        throw I18nValidationException(i18nKey = ACTIVITY_DESCRIPTION_SIZE_ERROR_KEY)

    if (!workPackageActivities.all { it.deliverables.all { it.description.all { (it.translation?.length ?: 0) <= 300 } } })
        throw I18nValidationException(i18nKey = DELIVERABLE_DESCRIPTION_LONG_ERROR_KEY)
}

fun validateWorkPackageActivityPartners(
    workPackageActivities: Collection<WorkPackageActivity>,
    availablePartnerIds: Set<Long>
) {
    val allPartnerIds = workPackageActivities.flatMapTo(HashSet()) { it.partnerIds }
    val notAvailablePartnerIds = allPartnerIds subtract availablePartnerIds

    if (notAvailablePartnerIds.isNotEmpty())
        throw PartnersNotFound(notAvailablePartnerIds)
}

fun validateWorkPackageActivityConfiguration(
    afConfig: Set<ApplicationFormFieldConfiguration>,
    activities:  List<WorkPackageActivity>) {
    val deliverables = afConfig
        .find { it.id == ApplicationFormFieldSetting.PROJECT_ACTIVITIES_DELIVERABLES.id }
    if (deliverables?.visibilityStatus == FieldVisibilityStatus.NONE
        && activities.any { it.deliverables.isNotEmpty() }) {
        throw I18nValidationException(i18nKey = DELIVERABLES_NOT_ENABLED_ERROR_KEY)
    }
}
