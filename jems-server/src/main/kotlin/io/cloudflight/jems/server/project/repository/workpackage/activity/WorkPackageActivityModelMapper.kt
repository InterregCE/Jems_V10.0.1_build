package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageDeliverableRow
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue

fun WorkPackageActivity.toEntity(workPackage: WorkPackageEntity, index: Int): WorkPackageActivityEntity {
    val workPackageActivityEntity = WorkPackageActivityEntity(
        id = id,
        workPackage = workPackage,
        activityNumber = index,
        translatedValues = mutableSetOf(),
        startPeriod = startPeriod,
        endPeriod = endPeriod,
        deliverables = mutableSetOf(),
        partners = mutableSetOf(),
        deactivated = deactivated
    )
    val deliverablesEntity = deliverables.toIndexedEntity(workPackageActivityEntity)
    return workPackageActivityEntity.apply {
        deliverables.addAll(deliverablesEntity)
        partners.addAll(partnerIds.map {
            WorkPackageActivityPartnerEntity(WorkPackageActivityPartnerId(this, it))
        }.toMutableSet())
        translatedValues.addTranslationEntities(
            { language ->
                WorkPackageActivityTranslationEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    title = title.extractTranslation(language),
                )
            }, arrayOf(description, title)
        )
    }
}

fun List<WorkPackageActivity>.toIndexedEntity(workPackage: WorkPackageEntity, shiftIndexBy: Int = 0) =
    mapIndexed { index, activity -> activity.toEntity(workPackage, index.plus(1).plus(shiftIndexBy)) }.toMutableList()

fun WorkPackageActivityDeliverable.toEntity(
    index: Int,
    workPackageActivityEntity: WorkPackageActivityEntity
): WorkPackageActivityDeliverableEntity {
    return WorkPackageActivityDeliverableEntity(
        id = id,
        deliverableNumber = index,
        translatedValues = mutableSetOf(),
        startPeriod = period,
        deactivated = deactivated,
        workPackageActivity = workPackageActivityEntity
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    title = title.extractTranslation(language),
                )
            }, arrayOf(description, title)
        )
    }
}

fun List<WorkPackageActivityDeliverable>.toIndexedEntity(workPackageActivityEntity: WorkPackageActivityEntity) =
    mapIndexedTo(HashSet()) { index, deliverable -> deliverable.toEntity(index.plus(1), workPackageActivityEntity) }

fun WorkPackageActivityEntity.toModel() =
    WorkPackageActivity(
        id = id,
        workPackageId = workPackage.id,
        workPackageNumber = workPackage.number!!,
        activityNumber = activityNumber,
        title = translatedValues.extractField { it.title },
        description = translatedValues.extractField { it.description },
        startPeriod = startPeriod,
        endPeriod = endPeriod,
        deliverables = deliverables.sortedBy { it.deliverableNumber }.map { it.toModel() },
        partnerIds = partners.map { it.id.projectPartnerId }.toSet(),
        deactivated = deactivated
    )

fun Iterable<WorkPackageActivityEntity>.toModel() = sortedBy { it.activityNumber }.map { it.toModel() }

fun WorkPackageActivityEntity.toSummaryModel() = WorkPackageActivitySummary (
    activityId = id,
    workPackageNumber = workPackage.number!!,
    activityNumber = activityNumber
)
fun WorkPackageActivity.toSummaryModel() = WorkPackageActivitySummary (
    activityId = id,
    workPackageNumber = workPackageNumber,
    activityNumber = activityNumber
)

fun Iterable<WorkPackageActivityEntity>.toSummaryModel() = map {
    it.toSummaryModel()
}

fun WorkPackageActivityDeliverableEntity.toModel() = WorkPackageActivityDeliverable(
    id = id,
    deliverableNumber = deliverableNumber,
    description = translatedValues.extractField { it.description },
    title = translatedValues.extractField { it.title },
    period = startPeriod,
    deactivated = deactivated
)

fun Set<WorkPackageActivityTranslationEntity>.toModel() = mapTo(HashSet()) {
    WorkPackageActivityTranslatedValue(
        language = it.translationId.language,
        title = it.title,
        description = it.description,
    )
}

fun List<WorkPackageActivityRow>.toActivityHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        WorkPackageActivity(
            id = groupedRows.value.first().id,
            workPackageId = groupedRows.value.first().workPackageId,
            workPackageNumber = groupedRows.value.first().workPackageNumber ?: 0,
            activityNumber = groupedRows.value.first().activityNumber,
            startPeriod = groupedRows.value.first().startPeriod,
            endPeriod = groupedRows.value.first().endPeriod,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description },
            deactivated = groupedRows.value.first().deactivated ?: false,
        )
    }

fun List<WorkPackageDeliverableRow>.toDeliverableHistoricalData() =
    this.groupBy { it.deliverableNumber }.map { groupedRows ->
        WorkPackageActivityDeliverable(
            id = groupedRows.value.first().id,
            deliverableNumber = groupedRows.value.first().deliverableNumber,
            period = groupedRows.value.first().startPeriod,
            description = groupedRows.value.extractField { it.description },
            title = groupedRows.value.extractField { it.title },
            deactivated = groupedRows.value.first().deactivated ?: false
        )
    }

fun List<WorkPackageActivityRow>.toTimePlanActivityHistoricalData() =
    this.groupBy { Pair(it.activityNumber, it.workPackageId) }.map { groupedRows ->
        WorkPackageActivity(
            id = groupedRows.value.first().id,
            workPackageId = groupedRows.value.first().workPackageId,
            activityNumber = groupedRows.value.first().activityNumber,
            startPeriod = groupedRows.value.first().startPeriod,
            endPeriod = groupedRows.value.first().endPeriod,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description },
            deactivated = groupedRows.value.first().deactivated ?: false
        )
    }

fun List<WorkPackageActivityPartnerRow>.toActivityPartnersHistoricalData() =
    this.map { it.projectPartnerId }.toSet()
