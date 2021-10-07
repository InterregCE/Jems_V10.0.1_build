package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageDetailRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput


fun WorkPackageEntity.toOutputWorkPackageSimple() = OutputWorkPackageSimple(
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) }
)

fun WorkPackageEntity.toOutputWorkPackage() = OutputWorkPackage(
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    specificObjective = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.specificObjective
        )
    },
    objectiveAndAudience = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.objectiveAndAudience
        )
    }
)

fun InputWorkPackageCreate.toEntity(project: ProjectEntity) = WorkPackageEntity(
    translatedValues = mutableSetOf(),
    project = project
).apply {
    translatedValues.addTranslationEntities(
        { language ->
            WorkPackageTransl(
                translationId = TranslationId(this, language),
                name = name.extractTranslation(language),
                specificObjective = specificObjective.extractTranslation(language),
                objectiveAndAudience = objectiveAndAudience.extractTranslation(language)
            )
        }, arrayOf(name, specificObjective, objectiveAndAudience)
    )
}

fun InputWorkPackageUpdate.toTranslatedValues(workPackageEntity: WorkPackageEntity): MutableSet<WorkPackageTransl> =
    mutableSetOf<WorkPackageTransl>().apply {
        this.addTranslationEntities(
            { language ->
                WorkPackageTransl(
                    translationId = TranslationId(workPackageEntity, language),
                    name = name.extractTranslation(language),
                    specificObjective = specificObjective.extractTranslation(language),
                    objectiveAndAudience = objectiveAndAudience.extractTranslation(language)
                )
            }, arrayOf(name, specificObjective, objectiveAndAudience)
        )
    }

fun List<WorkPackageRow>.toOutputWorkPackageHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        OutputWorkPackage(
            id = groupedRows.value.first().id,
            name = groupedRows.value.extractField { it.name },
            specificObjective = groupedRows.value.extractField { it.specificObjective },
            objectiveAndAudience = groupedRows.value.extractField { it.objectiveAndAudience },
            number = groupedRows.value.first().number,
        )
    }.first()

fun List<WorkPackageRow>.toOutputWorkPackageSimpleHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        OutputWorkPackageSimple(
            id = groupedRows.value.first().id,
            name = groupedRows.value.extractField { it.name },
            number = groupedRows.value.first().number,
        )
    }

fun List<WorkPackageOutputRow>.toWorkPackageOutputsHistoricalData() =
    this.groupBy { it.outputNumber }.map { groupedRows ->
        WorkPackageOutput(
            workPackageId = groupedRows.value.first().workPackageId,
            outputNumber = groupedRows.value.first().outputNumber,
            programmeOutputIndicatorId = groupedRows.value.first().programmeOutputIndicatorId,
            programmeOutputIndicatorIdentifier = groupedRows.value.first().programmeOutputIndicatorIdentifier,
            targetValue = groupedRows.value.first().targetValue,
            periodNumber = groupedRows.value.first().periodNumber,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description }
        )
    }

fun List<WorkPackageRow>.toTimePlanWorkPackageHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectWorkPackage(
            id = groupedRows.value.first().id,
            workPackageNumber = groupedRows.value.first().number!!,
            name = groupedRows.value.extractField { it.name },
            specificObjective = groupedRows.value.extractField { it.specificObjective },
            objectiveAndAudience = groupedRows.value.extractField { it.objectiveAndAudience }
        )
    }.toList()

fun List<WorkPackageOutputRow>.toTimePlanWorkPackageOutputHistoricalData() =
    this.groupBy { Pair(it.outputNumber, it.workPackageId) }.map { groupedRows ->
        WorkPackageOutput(
            workPackageId = groupedRows.value.first().workPackageId,
            outputNumber = groupedRows.value.first().outputNumber,
            programmeOutputIndicatorId = groupedRows.value.first().programmeOutputIndicatorId,
            programmeOutputIndicatorIdentifier = groupedRows.value.first().programmeOutputIndicatorIdentifier,
            targetValue = groupedRows.value.first().targetValue,
            periodNumber = groupedRows.value.first().periodNumber,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description }
        )
    }
fun List<WorkPackageDetailRow>.toModel()=
    groupBy { it.id }.map { groupedRows ->
        ProjectWorkPackageFull(
            id = groupedRows.key,
            workPackageNumber = groupedRows.value.first().number,
            name = groupedRows.value.extractField { it.name },
            specificObjective = groupedRows.value.extractField { it.specificObjective },
            objectiveAndAudience = groupedRows.value.extractField { it.objectiveAndAudience },
            activities = groupedRows.value.filter{it.activityId != null}.groupBy { it.activityId }.map { groupedActivityRows ->
                WorkPackageActivity(
                    id= groupedActivityRows.key!!,
                    workPackageId = groupedRows.key,
                    workPackageNumber = groupedRows.value.first().number,
                    activityNumber = groupedActivityRows.value.first().activityNumber!!,
                    title = groupedActivityRows.value.extractField({it.activityLanguage}) { it.activityTitle },
                    description = groupedActivityRows.value.extractField({it.activityLanguage}) { it.activityDescription},
                    startPeriod = groupedActivityRows.value.first().startPeriod,
                    endPeriod= groupedActivityRows.value.first().endPeriod,
                    deliverables = groupedActivityRows.value.filter { it.deliverableId != null }.groupBy { it.deliverableId }.map { groupedDeliverableRows ->
                        WorkPackageActivityDeliverable(
                            id = groupedDeliverableRows.key!!,
                            deliverableNumber = groupedDeliverableRows.value.first().deliverableNumber!!,
                            description = groupedDeliverableRows.value.extractField({it.deliverableLanguage}) { it.deliverableDescription },
                            period = groupedDeliverableRows.value.first().deliverableStartPeriod
                        )
                    },
                    partnerIds = groupedActivityRows.value.mapNotNullTo(hashSetOf()){it.partnerId}
                )
            },
            outputs = groupedRows.value.filter { it.outputNumber != null }.groupBy { it.outputNumber }.map { groupedOutputRows ->
                WorkPackageOutput(
                    workPackageId = groupedRows.key,
                    outputNumber = groupedOutputRows.value.first().outputNumber!!,
                    programmeOutputIndicatorId = groupedOutputRows.value.first().programmeOutputIndicatorId,
                    programmeOutputIndicatorIdentifier = groupedOutputRows.value.first().programmeOutputIndicatorIdentifier,
                    targetValue = groupedOutputRows.value.first().targetValue,
                    periodNumber = groupedOutputRows.value.first().outputPeriodNumber,
                    title = groupedOutputRows.value.extractField ({it.outputLanguage}){ it.outputTitle },
                    description = groupedOutputRows.value.extractField ({it.outputLanguage}){ it.outputDescription }
                )
            },
            investments = groupedRows.value.filter { it.investmentId != null }.groupBy { it.investmentId }.map { groupedInvestmentRow ->
                WorkPackageInvestment(
                    id = groupedInvestmentRow.key,
                    investmentNumber = groupedInvestmentRow.value.first().investmentNumber!!,
                    title = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.investmentTitle },
                    justificationExplanation = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.justificationExplanation },
                    justificationTransactionalRelevance = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.justificationTransactionalRelevance},
                    justificationBenefits = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.justificationBenefits },
                    justificationPilot = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.justificationPilot},
                    address = Address(
                        country = groupedInvestmentRow.value.first().investmentCountry,
                        nutsRegion2 = groupedInvestmentRow.value.first().investmentNutsRegion2,
                        nutsRegion3 = groupedInvestmentRow.value.first().investmentNutsRegion3,
                        street = groupedInvestmentRow.value.first().investmentStreet,
                        houseNumber = groupedInvestmentRow.value.first().investmentHouseNumber,
                        postalCode = groupedInvestmentRow.value.first().investmentPostalCode,
                        city = groupedInvestmentRow.value.first().investmentCity,
                    ),
                    risk = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.investmentRisk },
                    documentation = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.investmentDocumentation},
                    ownershipSiteLocation = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.ownershipSiteLocation },
                    ownershipRetain = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.ownershipRetain},
                    ownershipMaintenance = groupedInvestmentRow.value.extractField ({it.investmentLanguage}) { it.ownershipMaintenance},
                )
            }
        )
    }
