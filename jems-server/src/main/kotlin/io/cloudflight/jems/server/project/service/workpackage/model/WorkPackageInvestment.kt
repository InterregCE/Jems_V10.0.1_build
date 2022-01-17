package io.cloudflight.jems.server.project.service.workpackage.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.Address

data class WorkPackageInvestment(
    val id: Long?,
    val investmentNumber: Int,
    val title: Set<InputTranslation> = emptySet(),
    val expectedDeliveryPeriod: Int? = null,
    val justificationExplanation: Set<InputTranslation> = emptySet(),
    val justificationTransactionalRelevance: Set<InputTranslation> = emptySet(),
    val justificationBenefits: Set<InputTranslation> = emptySet(),
    val justificationPilot: Set<InputTranslation> = emptySet(),
    val address: Address?,
    val risk: Set<InputTranslation> = emptySet(),
    val documentation: Set<InputTranslation> = emptySet(),
    val documentationExpectedImpacts: Set<InputTranslation> = emptySet(),
    val ownershipSiteLocation: Set<InputTranslation> = emptySet(),
    val ownershipRetain: Set<InputTranslation> = emptySet(),
    val ownershipMaintenance: Set<InputTranslation> = emptySet(),
)
