package io.cloudflight.jems.server.project.controller.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageOutputDTO
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.update.UpdateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.update.UpdateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.update.UpdateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.update.UpdateProjectPartnerReportWorkPackageOutput

fun List<ProjectPartnerReportWorkPackage>.toDto() = map {
    ProjectPartnerReportWorkPackageDTO(
        id = it.id,
        number = it.number,
        description = it.description,
        activities = it.activities.toActivitiesDto(),
        outputs = it.outputs.toOutputsDto(),
    )
}

fun List<ProjectPartnerReportWorkPackageActivity>.toActivitiesDto() = map {
    ProjectPartnerReportWorkPackageActivityDTO(
        id = it.id,
        number = it.number,
        title = it.title,
        progress = it.progress,
        deliverables = it.deliverables.toDeliverablesDto(),
        attachment = it.attachment?.toDto(),
        deactivated = it.deactivated
    )
}

fun List<ProjectPartnerReportWorkPackageActivityDeliverable>.toDeliverablesDto() = map {
    ProjectPartnerReportWorkPackageActivityDeliverableDTO(
        id = it.id,
        number = it.number,
        title = it.title,
        contribution = it.contribution,
        evidence = it.evidence,
        attachment = it.attachment?.toDto(),
        deactivated = it.deactivated
    )
}

fun List<ProjectPartnerReportWorkPackageOutput>.toOutputsDto() = map {
    ProjectPartnerReportWorkPackageOutputDTO(
        id = it.id,
        number = it.number,
        title = it.title,
        contribution = it.contribution,
        evidence = it.evidence,
        attachment = it.attachment?.toDto(),
        deactivated = it.deactivated,
    )
}

fun List<UpdateProjectPartnerReportWorkPackageDTO>.toModel() = map {
    UpdateProjectPartnerReportWorkPackage(
        id = it.id,
        description = it.description,
        activities = it.activities.toModelActivities(),
        outputs = it.outputs.toModelOutputs(),
    )
}

fun List<UpdateProjectPartnerReportWorkPackageActivityDTO>.toModelActivities() = map {
    UpdateProjectPartnerReportWorkPackageActivity(
        id = it.id,
        progress = it.progress,
        deliverables = it.deliverables.toModelDeliverables(),
    )
}

fun List<UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO>.toModelDeliverables() = map {
    UpdateProjectPartnerReportWorkPackageActivityDeliverable(
        id = it.id,
        contribution = it.contribution,
        evidence = it.evidence,
    )
}

fun List<UpdateProjectPartnerReportWorkPackageOutputDTO>.toModelOutputs() = map {
    UpdateProjectPartnerReportWorkPackageOutput(
        id = it.id,
        contribution = it.contribution,
        evidence = it.evidence,
    )
}
