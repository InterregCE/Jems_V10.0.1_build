package io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityDeliverableCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageInvestmentCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageOutputCreate
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import java.math.BigDecimal


fun List<ProjectWorkPackageFull>.toCreateEntity() = map { wp ->
    CreateProjectPartnerReportWorkPackage(
        workPackageId = wp.id,
        number = wp.workPackageNumber,
        deactivated = wp.deactivated,
        specificObjective = wp.specificObjective,
        communicationObjective = wp.objectiveAndAudience,
        activities = wp.activities.map { a ->
            CreateProjectPartnerReportWorkPackageActivity(
                activityId = a.id,
                number = a.activityNumber,
                title = a.title,
                deactivated = a.deactivated,
                startPeriodNumber = a.startPeriod,
                endPeriodNumber = a.endPeriod,
                deliverables = a.deliverables.map { d ->
                    CreateProjectPartnerReportWorkPackageActivityDeliverable(
                        deliverableId = d.id,
                        number = d.deliverableNumber,
                        title = d.title,
                        deactivated = d.deactivated,
                        periodNumber = d.period,
                        previouslyReported = null,
                    )
                },
            )
        },
        outputs = wp.outputs.map { o ->
            CreateProjectPartnerReportWorkPackageOutput(
                number = o.outputNumber,
                title = o.title,
                deactivated = o.deactivated,
                programmeOutputIndicatorId = o.programmeOutputIndicatorId,
                periodNumber = o.periodNumber,
                targetValue = o.targetValue ?: BigDecimal.ZERO,
                previouslyReported = null,
            )
        },
    )
}

fun List<ProjectWorkPackageFull>.toCreateEntity(
    previouslyReportedDeliverables: Map<Int, Map<Int, Map<Int, BigDecimal>>> = emptyMap(),
    previouslyReportedOutputs: Map<Int, Map<Int, BigDecimal>> = emptyMap(),
    lastWorkPlan: List<ProjectReportWorkPackage> = emptyList(),
) = map { wp ->
    ProjectReportWorkPackageCreate(
        workPackageId = wp.id,
        number = wp.workPackageNumber,
        deactivated = wp.deactivated,
        specificObjective = wp.specificObjective,
        specificStatus = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.specificStatus,
        communicationObjective = wp.objectiveAndAudience,
        communicationStatus = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.communicationStatus,
        completed = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.completed ?: false,
        activities = wp.activities.map { a ->
            val previousActivity = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }
                ?.activities?.firstOrNull { it.number == a.activityNumber }
            ProjectReportWorkPackageActivityCreate(
                activityId = a.id,
                number = a.activityNumber,
                title = a.title,
                deactivated = a.deactivated,
                startPeriodNumber = a.startPeriod,
                endPeriodNumber = a.endPeriod,
                status = previousActivity?.status,
                deliverables = a.deliverables.map { d ->
                    val previousDeliverable = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }
                        ?.activities?.firstOrNull { it.number == a.activityNumber }
                        ?.deliverables?.firstOrNull { it.number == d.deliverableNumber }
                    ProjectReportWorkPackageActivityDeliverableCreate(
                        deliverableId = d.id,
                        number = d.deliverableNumber,
                        title = d.title,
                        deactivated = d.deactivated,
                        periodNumber = d.period,
                        previouslyReported = previouslyReportedDeliverables[wp.workPackageNumber]
                            ?.get(a.activityNumber)?.get(d.deliverableNumber) ?: BigDecimal.ZERO,
                        previousCurrentReport = previousDeliverable?.currentReport ?: BigDecimal.ZERO,
                        currentReport = previousDeliverable?.currentReport ?: BigDecimal.ZERO,
                        previousProgress = previousDeliverable?.progress ?: emptySet(),
                        progress =previousDeliverable?.progress ?: emptySet()
                        )
                },
                previousProgress = previousActivity?.progress ?: emptySet(),
                previousStatus = previousActivity?.status,
                progress = previousActivity?.progress ?: emptySet()
            )
        },
        outputs = wp.outputs.map { o ->
            val previousOutput = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }
                ?.outputs?.firstOrNull { it.number == o.outputNumber }
            ProjectReportWorkPackageOutputCreate(
                number = o.outputNumber,
                title = o.title,
                deactivated = o.deactivated,
                programmeOutputIndicatorId = o.programmeOutputIndicatorId,
                periodNumber = o.periodNumber,
                targetValue = o.targetValue ?: BigDecimal.ZERO,
                previouslyReported = previouslyReportedOutputs[wp.workPackageNumber]?.get(o.outputNumber) ?: BigDecimal.ZERO,
                progress = previousOutput?.progress ?: emptySet(),
                previousProgress = previousOutput?.progress ?: emptySet(),
                previousCurrentReport = previousOutput?.currentReport ?: BigDecimal.ZERO,
                currentReport = previousOutput?.currentReport ?: BigDecimal.ZERO
                )
        },
        investments = wp.investments.map {i ->
            val previousInvestment = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }
                ?.investments?.firstOrNull { it.number == i.investmentNumber }
            ProjectReportWorkPackageInvestmentCreate(
                investmentId = i.id,
                number = i.investmentNumber,
                title = i.title,
                expectedDeliveryPeriod = i.expectedDeliveryPeriod,
                justificationExplanation = i.justificationExplanation,
                justificationTransactionalRelevance = i.justificationTransactionalRelevance,
                justificationBenefits = i.justificationBenefits,
                justificationPilot = i.justificationPilot,
                address = i.address,
                risk = i.risk,
                documentation = i.documentation,
                documentationExpectedImpacts = i.documentationExpectedImpacts,
                ownershipSiteLocation = i.ownershipSiteLocation,
                ownershipRetain = i.ownershipRetain,
                ownershipMaintenance = i.ownershipMaintenance,
                deactivated = i.deactivated,
                progress = previousInvestment?.progress ?: emptySet(),
                previousProgress = previousInvestment?.progress ?: emptySet()
                )
        },
        previousCommunicationExplanation = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.communicationExplanation ?: emptySet(),
        previousSpecificExplanation = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.specificExplanation ?: emptySet(),
        previousSpecificStatus = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.specificStatus,
        previousCompleted = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.completed ?: false,
        previousCommunicationStatus = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.communicationStatus    ,
        communicationExplanation = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.communicationExplanation ?: emptySet(),
        specificExplanation = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.specificExplanation ?: emptySet(),
        previousDescription = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.description ?: emptySet(),
        description = lastWorkPlan.firstOrNull { it.number == wp.workPackageNumber }?.description ?: emptySet()
    )
}
