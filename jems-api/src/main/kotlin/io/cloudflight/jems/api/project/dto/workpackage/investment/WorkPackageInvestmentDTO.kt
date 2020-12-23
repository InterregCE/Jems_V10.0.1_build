package io.cloudflight.jems.api.project.dto.workpackage.investment

import io.cloudflight.jems.api.common.dto.AddressDTO

data class WorkPackageInvestmentDTO(
    val id: Long?,
    val investmentNumber: Int,
    val title: String? = null,
    val justificationExplanation: String? = null,
    val justificationTransactionalRelevance: String? = null,
    val justificationBenefits: String? = null,
    val justificationPilot: String? = null,
    val address: AddressDTO?,
    val risk: String? = null,
    val documentation: Int? = null,
    val ownershipSiteLocation: String? = null,
    val ownershipRetain: String? = null,
    val ownershipMaintenance: String? = null
)
