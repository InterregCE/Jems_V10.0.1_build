package io.cloudflight.jems.server.project.service.workpackage.activity

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.PartnersNotFound

private const val MAX_ALLOWED_ACTIVITIES = 20
private const val MAX_ALLOWED_DELIVERABLES = 20

fun validateWorkPackageActivities(workPackageActivities: Collection<WorkPackageActivity>) {
    if (workPackageActivities.size > MAX_ALLOWED_ACTIVITIES)
        throw I18nValidationException(i18nKey = "workPackage.activity.max.allowed.reached")
    if (!workPackageActivities.all { it.deliverables.size <= MAX_ALLOWED_DELIVERABLES })
        throw I18nValidationException(i18nKey = "workPackage.activity.deliverables.max.allowed.reached")

    val isStartAlwaysBeforeEnd = workPackageActivities
        .filter { it.startPeriod != null && it.endPeriod != null }
        .all { it.startPeriod!! <= it.endPeriod!! }
    if (!isStartAlwaysBeforeEnd)
        throw I18nValidationException(i18nKey = "workPackage.activity.startPeriod.is.after.endPeriod")

    if (!workPackageActivities.all { it.translatedValues.all { (it.title?.length ?: 0) <= 200 } })
        throw I18nValidationException(i18nKey = "workPackage.activity.title.size.too.long")
    if (!workPackageActivities.all { it.translatedValues.all { (it.description?.length ?: 0) <= 500 } })
        throw I18nValidationException(i18nKey = "workPackage.activity.description.size.too.long")

    if (!workPackageActivities.all { it.deliverables.all { it.translatedValues.all { (it.description?.length ?: 0) <= 200 } } })
        throw I18nValidationException(i18nKey = "workPackage.activity.deliverable.description.size.too.long")
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
