package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableTranslEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputTranslEntity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput

fun CreateProjectPartnerReportWorkPackage.toEntity(report: ProjectPartnerReportEntity) =
    ProjectPartnerReportWorkPackageEntity(
        reportEntity = report,
        number = number,
        workPackageId = workPackageId,
    )

fun CreateProjectPartnerReportWorkPackageActivity.toEntity(wp: ProjectPartnerReportWorkPackageEntity) =
    ProjectPartnerReportWorkPackageActivityEntity(
        workPackageEntity = wp,
        number = number,
        activityId = activityId,
        translatedValues = mutableSetOf(),
    ).apply {
        translatedValues.addAll(
            title.filter { !it.translation.isNullOrBlank() }
                .mapTo(HashSet()) {
                    ProjectPartnerReportWorkPackageActivityTranslEntity(
                        TranslationId(this, it.language),
                        title = it.translation,
                        description = null,
                    )
                }
        )
    }

fun List<CreateProjectPartnerReportWorkPackageOutput>.toEntity(wp: ProjectPartnerReportWorkPackageEntity) =
    map {
        ProjectPartnerReportWorkPackageOutputEntity(
            workPackageEntity = wp,
            number = it.number,
            contribution = null,
            evidence = null,
            translatedValues = mutableSetOf(),
        ).apply {
            translatedValues.addAll(
                it.title.filter { !it.translation.isNullOrBlank() }
                    .mapTo(HashSet()) {
                        ProjectPartnerReportWorkPackageOutputTranslEntity(
                            TranslationId(this, it.language),
                            title = it.translation,
                        )
                    }
            )
        }
    }

fun List<CreateProjectPartnerReportWorkPackageActivityDeliverable>.toEntity(
    activity: ProjectPartnerReportWorkPackageActivityEntity,
) = map {
    ProjectPartnerReportWorkPackageActivityDeliverableEntity(
        activityEntity = activity,
        number = it.number,
        deliverableId = it.deliverableId,
        contribution = null,
        evidence = null,
        translatedValues = mutableSetOf(),
    ).apply {
        translatedValues.addAll(
            it.title.filter { !it.translation.isNullOrBlank() }
                .mapTo(HashSet()) {
                    ProjectPartnerReportWorkPackageActivityDeliverableTranslEntity(
                        TranslationId(this, it.language),
                        title = it.translation,
                    )
                }
        )
    }
}
