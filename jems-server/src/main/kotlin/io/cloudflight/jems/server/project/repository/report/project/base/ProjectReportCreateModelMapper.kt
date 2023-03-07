package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageTranslEntity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import java.math.BigDecimal

fun ProjectReportWorkPackageCreate.toEntity(report: ProjectReportEntity) =
    ProjectReportWorkPackageEntity(
        reportEntity = report,
        number = number,
        deactivated = deactivated,
        workPackageId = workPackageId,
        specificStatus = specificStatus,
        communicationStatus = communicationStatus,
        completed = completed,
    ).apply {
        val specMap = specificObjective.associateBy( { it.language }, { it.translation } )
        val commMap = communicationObjective.associateBy( { it.language }, { it.translation } )

        val languages = specMap.keys union commMap.keys

        translatedValues.addAll(
            languages.map {
                ProjectReportWorkPackageTranslEntity(
                    TranslationId(this, it),
                    specificObjective = specMap[it] ?: "",
                    specificExplanation = "",
                    communicationObjective = commMap[it] ?: "",
                    communicationExplanation = "",
                    description = "",
                )
            }
        )
    }

fun ProjectReportWorkPackageActivityCreate.toEntity(wp: ProjectReportWorkPackageEntity) =
    ProjectReportWorkPackageActivityEntity(
        workPackageEntity = wp,
        number = number,
        deactivated = deactivated,
        activityId = activityId,
        startPeriodNumber = startPeriodNumber,
        endPeriodNumber = endPeriodNumber,
        status = status,
        attachment = null,
    ).apply {
        val translMap = title.associateBy( { it.language }, { it.translation } )

        translatedValues.addAll(
            translMap.keys.map {
                ProjectReportWorkPackageActivityTranslEntity(
                    TranslationId(this, it),
                    title = translMap[it] ?: "",
                    progress = "",
                )
            }
        )
    }

fun List<CreateProjectPartnerReportWorkPackageActivityDeliverable>.toEntity(
    activity: ProjectReportWorkPackageActivityEntity,
) = map {
    ProjectReportWorkPackageActivityDeliverableEntity(
        activityEntity = activity,
        number = it.number,
        deactivated = it.deactivated,
        deliverableId = it.deliverableId,
        periodNumber = it.periodNumber,
        previouslyReported = it.previouslyReported ?: BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO,
        attachment = null,
    ).apply {
        val translMap = it.title.associateBy({ it.language }, { it.translation })

        translatedValues.addAll(
            translMap.keys.map {
                ProjectReportWorkPackageActivityDeliverableTranslEntity(
                    TranslationId(this, it),
                    title = translMap[it] ?: "",
                    progress = "",
                )
            }
        )
    }
}

fun List<CreateProjectPartnerReportWorkPackageOutput>.toEntity(
    wp: ProjectReportWorkPackageEntity,
    indicatorResolver: (Long) -> OutputIndicatorEntity,
) =
    map {
        ProjectReportWorkPackageOutputEntity(
            workPackageEntity = wp,
            number = it.number,
            deactivated = it.deactivated,
            programmeOutputIndicator = it.programmeOutputIndicatorId?.let { indicatorResolver.invoke(it) },
            periodNumber = it.periodNumber,
            targetValue = it.targetValue,
            previouslyReported = it.previouslyReported ?: BigDecimal.ZERO,
            currentReport = BigDecimal.ZERO,
            attachment = null,
        ).apply {
            translatedValues.addAll(
                it.title.map {
                    ProjectReportWorkPackageOutputTranslEntity(
                        TranslationId(this, it.language),
                        title = it.translation ?: "",
                        progress = "",
                    )
                }
            )
        }
    }
