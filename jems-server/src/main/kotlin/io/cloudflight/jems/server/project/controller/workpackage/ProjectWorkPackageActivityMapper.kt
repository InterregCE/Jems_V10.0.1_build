package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDeliverableDTO
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

fun WorkPackageActivityDeliverableDTO.toDeliverableModel() = WorkPackageActivityDeliverable(
    description = description,
    period = period,
)

fun List<WorkPackageActivityDeliverableDTO>.toDeliverableModel() = map { it.toDeliverableModel() }


fun WorkPackageActivity.toDto() = WorkPackageActivityDTO(
    workPackageId = workPackageId,
    activityNumber = activityNumber,
    title = title,
    startPeriod = startPeriod,
    endPeriod = endPeriod,
    description = description,
    deliverables = deliverables.toDeliverableDto(),
    partnerIds = partnerIds,
)

fun List<WorkPackageActivity>.toDto() = map { it.toDto() }

fun WorkPackageActivityDeliverable.toDeliverableDto() = WorkPackageActivityDeliverableDTO(
    deliverableNumber = deliverableNumber,
    description = description,
    period = period,
)

fun List<WorkPackageActivityDeliverable>.toDeliverableDto() = map { it.toDeliverableDto() }
