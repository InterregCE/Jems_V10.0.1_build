package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageTranslEntity
import io.cloudflight.jems.server.project.repository.workpackage.toAddressEntity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportInvestment
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageInvestmentCreate
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

fun List<ProjectReportWorkPackageInvestmentCreate>.toEntity(
    wp: ProjectReportWorkPackageEntity
) =
    map {
        ProjectReportWorkPackageInvestmentEntity(
            workPackageEntity = wp,
            number = it.number,
            deactivated = it.deactivated,
            expectedDeliveryPeriod = it.expectedDeliveryPeriod,
            address = it.address?.toAddressEntity(),
        ).apply {
            val titleMap = it.title.associateBy( { it.language }, { it.translation } )
            val jEMap = it.justificationExplanation.associateBy( { it.language }, { it.translation } )
            val jTRMap = it.justificationTransactionalRelevance.associateBy( { it.language }, { it.translation } )
            val jBMap = it.justificationBenefits.associateBy( { it.language }, { it.translation } )
            val jPMap = it.justificationPilot.associateBy( { it.language }, { it.translation } )
            val rMap = it.risk.associateBy( { it.language }, { it.translation } )
            val dMap = it.documentation.associateBy( { it.language }, { it.translation } )
            val dEIMap = it.documentationExpectedImpacts.associateBy( { it.language }, { it.translation } )
            val oSLMap = it.ownershipSiteLocation.associateBy( { it.language }, { it.translation } )
            val oRMap = it.ownershipRetain.associateBy( { it.language }, { it.translation } )
            val oMMap = it.ownershipMaintenance.associateBy( { it.language }, { it.translation } )

            val languages = titleMap.keys union jEMap.keys union jTRMap.keys union jBMap.keys union jPMap.keys union rMap.keys union dMap.keys union dEIMap.keys union oSLMap.keys union oRMap.keys union oMMap.keys

            translatedValues.addAll(
                languages.map {
                    ProjectReportWorkPackageInvestmentTranslEntity(
                        TranslationId(this, it),
                        title = titleMap[it] ?: "",
                        justificationExplanation = jEMap[it] ?: "",
                        justificationTransactionalRelevance = jTRMap[it] ?: "",
                        justificationBenefits = jBMap[it] ?: "",
                        justificationPilot = jPMap[it] ?: "",
                        risk = rMap[it] ?: "",
                        documentation = dMap[it] ?: "",
                        documentationExpectedImpacts = dEIMap[it] ?: "",
                        ownershipSiteLocation = oSLMap[it] ?: "",
                        ownershipRetain = oRMap[it] ?: "",
                        ownershipMaintenance = oMMap[it] ?: "",
                        progress = "",
                    )
                }
            )
        }
    }

fun ProjectReportInvestment.toEntity(
    report: ProjectReportEntity,
) = ReportProjectCertificateInvestmentEntity(
    reportEntity = report,
    investmentId = investmentId,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackageNumber,
    translatedValues = mutableSetOf(),
    deactivated = deactivated,
    total = total,
    current = BigDecimal.ZERO,
    previouslyReported = previouslyReported,
).apply {
    translatedValues.addAll(
        combineInvestmentTranslatedValues(this, title)
    )
}

fun combineInvestmentTranslatedValues(
    sourceEntity: ReportProjectCertificateInvestmentEntity,
    title: Set<InputTranslation>,
): MutableSet<ReportProjectCertificateInvestmentTranslEntity> {
    val titleMap = title.filter { !it.translation.isNullOrBlank() }
        .associateBy( { it.language }, { it.translation } )

    return titleMap.keys.mapTo(HashSet()) {
        ReportProjectCertificateInvestmentTranslEntity(
            TranslationId(sourceEntity, it),
            title = titleMap[it]!!,
        )
    }
}

