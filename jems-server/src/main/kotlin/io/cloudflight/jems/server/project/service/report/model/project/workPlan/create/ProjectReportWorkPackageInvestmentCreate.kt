package io.cloudflight.jems.server.project.service.report.model.project.workPlan.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus

data class ProjectReportWorkPackageInvestmentCreate(
    val investmentId: Long?,
    val number: Int,
    val title: Set<InputTranslation>,
    val expectedDeliveryPeriod: Int?,
    val justificationExplanation: Set<InputTranslation>,
    val justificationTransactionalRelevance: Set<InputTranslation>,
    val justificationBenefits: Set<InputTranslation>,
    val justificationPilot: Set<InputTranslation>,
    val address: Address?,
    val risk: Set<InputTranslation>,
    val documentation: Set<InputTranslation>,
    val documentationExpectedImpacts: Set<InputTranslation>,
    val ownershipSiteLocation: Set<InputTranslation>,
    val ownershipRetain: Set<InputTranslation>,
    val ownershipMaintenance: Set<InputTranslation>,
    val deactivated: Boolean,
    val previousProgress: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
)
