package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageDeliverableRow
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue

fun WorkPackageActivity.toEntity(workPackageId: Long, index: Int): WorkPackageActivityEntity {
    val activityId = WorkPackageActivityId(workPackageId, index)
    return WorkPackageActivityEntity(
        activityId = activityId,
        translatedValues = mutableSetOf(),
        startPeriod = startPeriod,
        endPeriod = endPeriod,
        deliverables = deliverables.toIndexedEntity(activityId),
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                WorkPackageActivityTranslationEntity(
                    translationId = WorkPackageActivityTranslationId(this, language),
                    description = description.extractTranslation(language),
                    title = title.extractTranslation(language),
                )
            }, arrayOf(description, title)
        )
    }
}

fun List<WorkPackageActivity>.toIndexedEntity(workPackageId: Long) =
    mapIndexed { index, activity -> activity.toEntity(workPackageId, index.plus(1)) }

fun WorkPackageActivityDeliverable.toEntity(
    activityId: WorkPackageActivityId,
    index: Int
): WorkPackageActivityDeliverableEntity {
    val deliverableId = WorkPackageActivityDeliverableId(activityId = activityId, deliverableNumber = index)
    return WorkPackageActivityDeliverableEntity(
        deliverableId = deliverableId,
        translatedValues = mutableSetOf(),
        startPeriod = period,
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = WorkPackageActivityDeliverableTranslationId(this, language),
                    description = description.extractTranslation(language),
                )
            }, arrayOf(description)
        )
    }
}

fun List<WorkPackageActivityDeliverable>.toIndexedEntity(activityId: WorkPackageActivityId) =
    mapIndexedTo(HashSet()) { index, deliverable -> deliverable.toEntity(activityId, index.plus(1)) }

fun WorkPackageActivityEntity.toModel(partnersByActivities: Map<WorkPackageActivityId, List<Long>>) =
    WorkPackageActivity(
        workPackageId = activityId.workPackageId,
        activityNumber = activityId.activityNumber,
        title = translatedValues.extractField { it.title },
        description = translatedValues.extractField { it.description },
        startPeriod = startPeriod,
        endPeriod = endPeriod,
        deliverables = deliverables.sortedBy { it.deliverableId.deliverableNumber }.map { it.toModel() },
        partnerIds = partnersByActivities[activityId]?.toSet() ?: emptySet()
    )

fun Iterable<WorkPackageActivityEntity>.toModel(
    partnersByActivities: Map<WorkPackageActivityId, List<Long>>
) = sortedBy { it.activityId.activityNumber }.map { it.toModel(partnersByActivities) }

fun WorkPackageActivityDeliverableEntity.toModel() = WorkPackageActivityDeliverable(
    deliverableNumber = deliverableId.deliverableNumber,
    description = translatedValues.extractField { it.description },
    period = startPeriod,
)

fun Set<WorkPackageActivityTranslationEntity>.toModel() = mapTo(HashSet()) {
    WorkPackageActivityTranslatedValue(
        language = it.translationId.language,
        title = it.title,
        description = it.description,
    )
}

fun List<WorkPackageActivityRow>.toActivityHistoricalData() =
    this.groupBy { it.activityNumber }.map { groupedRows ->
        WorkPackageActivity(
            workPackageId = groupedRows.value.first().workPackageId,
            activityNumber = groupedRows.value.first().activityNumber,
            startPeriod = groupedRows.value.first().startPeriod,
            endPeriod = groupedRows.value.first().endPeriod,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description }
        )
    }

fun List<WorkPackageDeliverableRow>.toDeliverableHistoricalData() =
    this.groupBy { it.deliverableNumber }.map { groupedRows ->
        WorkPackageActivityDeliverable(
            deliverableNumber = groupedRows.value.first().deliverableNumber,
            period = groupedRows.value.first().startPeriod,
            description = groupedRows.value.extractField { it.description }
        )
    }

fun List<WorkPackageActivityRow>.toTimePlanActivityHistoricalData() =
    this.groupBy { Pair(it.activityNumber, it.workPackageId) }.map { groupedRows ->
        WorkPackageActivity(
            workPackageId = groupedRows.value.first().workPackageId,
            activityNumber = groupedRows.value.first().activityNumber,
            startPeriod = groupedRows.value.first().startPeriod,
            endPeriod = groupedRows.value.first().endPeriod,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description }
        )
    }

fun List<WorkPackageActivityPartnerRow>.toActivityPartnersHistoricalData() =
    this.map { it.projectPartnerId }.toSet()
