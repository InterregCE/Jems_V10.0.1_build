package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportVerificationClarificationEntity
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
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportInvestment
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityDeliverableCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageInvestmentCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageOutputCreate
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
        previousSpecificStatus = specificStatus,
        previousCommunicationStatus = communicationStatus,
        previousCompleted = completed
    ).apply {
        val specMap = specificObjective.associateBy( { it.language }, { it.translation } )
        val commMap = communicationObjective.associateBy( { it.language }, { it.translation } )
        val specExpMap = specificExplanation.associateBy( { it.language }, { it.translation } )
        val commExpMap = communicationExplanation.associateBy( { it.language }, { it.translation } )
        val descMap = description.associateBy( { it.language }, { it.translation } )

        val languages = specMap.keys union commMap.keys union specExpMap.keys union commExpMap.keys union descMap.keys

        translatedValues.addAll(
            languages.map {
                ProjectReportWorkPackageTranslEntity(
                    TranslationId(this, it),
                    specificObjective = specMap[it] ?: "",
                    specificExplanation = specExpMap[it] ?: "",
                    communicationObjective = commMap[it] ?: "",
                    communicationExplanation = commExpMap[it] ?: "",
                    description = descMap[it] ?: "",
                    previousCommunicationExplanation = commExpMap[it] ?: "",
                    previousSpecificExplanation = specExpMap[it] ?: "",
                    previousDescription = descMap[it] ?: ""
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
        previousStatus = status,
    ).apply {
        val translMap = title.associateBy( { it.language }, { it.translation } )
        val progMap = progress.associateBy( { it.language }, { it.translation } )

        val languages = translMap.keys union progMap.keys

        translatedValues.addAll(
            languages.map {
                ProjectReportWorkPackageActivityTranslEntity(
                    TranslationId(this, it),
                    title = translMap[it] ?: "",
                    progress = progMap[it] ?: "",
                    previousProgress = progMap[it] ?: ""
                )
            }
        )
    }

fun List<ProjectReportWorkPackageActivityDeliverableCreate>.toEntity(
    activity: ProjectReportWorkPackageActivityEntity,
) = map {
    ProjectReportWorkPackageActivityDeliverableEntity(
        activityEntity = activity,
        number = it.number,
        deactivated = it.deactivated,
        deliverableId = it.deliverableId,
        periodNumber = it.periodNumber,
        previouslyReported = it.previouslyReported,
        currentReport = BigDecimal.ZERO,
        attachment = null,
        previousCurrentReport = BigDecimal.ZERO
    ).apply {
        val translMap = it.title.associateBy( { it.language }, { it.translation } )
        val progMap = it.progress.associateBy( { it.language }, { it.translation } )

        val languages = translMap.keys union progMap.keys

        translatedValues.addAll(
            languages.map {
                ProjectReportWorkPackageActivityDeliverableTranslEntity(
                    TranslationId(this, it),
                    title = translMap[it] ?: "",
                    progress = progMap[it] ?: "",
                    previousProgress = progMap[it] ?: ""
                )
            }
        )
    }
}

fun List<ProjectReportWorkPackageOutputCreate>.toEntity(
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
            previousCurrentReport = BigDecimal.ZERO
        ).apply {
            val translMap = it.title.associateBy( { it.language }, { it.translation } )
            val progMap = it.progress.associateBy( { it.language }, { it.translation } )

            val languages = translMap.keys union progMap.keys

            translatedValues.addAll(
                languages.map {
                    ProjectReportWorkPackageOutputTranslEntity(
                        TranslationId(this, it),
                        title = translMap[it] ?: "",
                        progress = progMap[it] ?: "",
                        previousProgress = progMap[it] ?: ""
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
            val pMap = it.progress.associateBy( { it.language }, { it.translation } )

            val languages = titleMap.keys union jEMap.keys union jTRMap.keys union jBMap.keys union jPMap.keys union rMap.keys union dMap.keys union dEIMap.keys union oSLMap.keys union oRMap.keys union oMMap.keys union pMap.keys

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
                        progress =  pMap[it] ?: "",
                        previousProgress =  pMap[it] ?: ""
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
    previouslyVerified = previouslyVerified,
    currentVerified = BigDecimal.ZERO,
).apply {
    translatedValues.addAll(
        combineInvestmentTranslatedValues(this, title)
    )
}

fun ProjectReportVerificationClarificationEntity.toModel() = ProjectReportVerificationClarification(
    id = id,
    number = number,
    requestDate = requestDate,
    answerDate = answerDate,
    comment = comment
)

fun ProjectReportVerificationClarification.toEntity(projectReport: ProjectReportEntity) = ProjectReportVerificationClarificationEntity(
    id = id,
    number = number,
    projectReport = projectReport,
    requestDate = requestDate,
    answerDate = answerDate,
    comment = comment
)

private fun combineInvestmentTranslatedValues(
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
