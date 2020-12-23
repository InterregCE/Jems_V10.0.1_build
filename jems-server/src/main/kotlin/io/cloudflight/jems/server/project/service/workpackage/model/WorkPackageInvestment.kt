package io.cloudflight.jems.server.project.service.workpackage.model

import io.cloudflight.jems.server.project.service.model.Address

data class WorkPackageInvestment(
    val id: Long?,
    val investmentNumber: Int,
    val title: String? = null,
    val justificationExplanation: String? = null,
    val justificationTransactionalRelevance: String? = null,
    val justificationBenefits: String? = null,
    val justificationPilot: String? = null,
    val address: Address?,
    val risk: String? = null,
    val documentation: Int? = null,
    val ownershipSiteLocation: String? = null,
    val ownershipRetain: String? = null,
    val ownershipMaintenance: String? = null,
)
