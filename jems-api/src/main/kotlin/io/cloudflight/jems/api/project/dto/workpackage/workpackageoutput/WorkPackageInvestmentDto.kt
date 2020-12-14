package io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput

import io.cloudflight.jems.api.common.dto.AddressDTO
import java.util.*

data class WorkPackageInvestmentDTO(
    val id: UUID?,
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
