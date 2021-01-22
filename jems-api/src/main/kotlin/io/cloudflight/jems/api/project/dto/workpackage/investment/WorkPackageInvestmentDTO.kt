package io.cloudflight.jems.api.project.dto.workpackage.investment

import io.cloudflight.jems.api.common.dto.AddressDTO
import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageInvestmentDTO(
    val id: Long?,
    val investmentNumber: Int,
    val title: Set<InputTranslation> = emptySet(),
    val justificationExplanation: Set<InputTranslation> = emptySet(),
    val justificationTransactionalRelevance: Set<InputTranslation> = emptySet(),
    val justificationBenefits: Set<InputTranslation> = emptySet(),
    val justificationPilot: Set<InputTranslation> = emptySet(),
    val address: AddressDTO?,
    val risk: Set<InputTranslation> = emptySet(),
    val documentation: Set<InputTranslation> = emptySet(),
    val ownershipSiteLocation: Set<InputTranslation> = emptySet(),
    val ownershipRetain: Set<InputTranslation> = emptySet(),
    val ownershipMaintenance: Set<InputTranslation> = emptySet()
)
