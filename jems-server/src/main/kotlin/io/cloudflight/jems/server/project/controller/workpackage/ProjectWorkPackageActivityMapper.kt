package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivitySummaryDTO
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable

fun WorkPackageActivityDTO.toModel(workPackageId: Long) = WorkPackageActivity(
    workPackageId = workPackageId,
    title = title,
    description = description,
    startPeriod = startPeriod,
    endPeriod = endPeriod,
    deliverables = deliverables.toDeliverableModel(),
    partnerIds = partnerIds
)

fun List<WorkPackageActivityDTO>.toModel(workPackageId: Long) = map { it.toModel(workPackageId) }

fun WorkPackageActivityDeliverableDTO.toDeliverableModel(number: Int) = WorkPackageActivityDeliverable(
    deliverableId = deliverableId,
    deliverableNumber = number,
    description = description,
    period = period
)

fun List<WorkPackageActivityDeliverableDTO>.toDeliverableModel() = mapIndexed { index, it -> it.toDeliverableModel(index.plus(1)) }


fun WorkPackageActivity.toDto() = WorkPackageActivityDTO(
    id = activityId,
    workPackageId = workPackageId,
    activityNumber = activityNumber,
    title = title,
    startPeriod = startPeriod,
    endPeriod = endPeriod,
    description = description,
    deliverables = deliverables.toDeliverableDto(activityId),
    partnerIds = partnerIds,
)

fun List<WorkPackageActivity>.toDto() = map { it.toDto() }

fun WorkPackageActivityDeliverable.toDeliverableDto(activityId: Long) = WorkPackageActivityDeliverableDTO(
    activityId = activityId,
    deliverableId = deliverableId,
    deliverableNumber = deliverableNumber,
    description = description,
    period = period
)

fun List<WorkPackageActivityDeliverable>.toDeliverableDto(activityId: Long) = map { it.toDeliverableDto(activityId) }

fun WorkPackageActivitySummary.toDto() = WorkPackageActivitySummaryDTO(
    activityId = activityId,
    workPackageNumber = workPackageNumber,
    activityNumber = activityNumber
)

fun List<WorkPackageActivitySummary>.toSummariesDto() = this.map { it.toDto() }
