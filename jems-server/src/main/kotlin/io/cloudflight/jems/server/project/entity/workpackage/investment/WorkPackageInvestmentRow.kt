package io.cloudflight.jems.server.project.entity.workpackage.investment

import io.cloudflight.jems.server.common.entity.TranslationView

interface WorkPackageInvestmentRow: TranslationView {
    val id: Long
    val investmentNumber: Int
    val title: String?
    val expectedDeliveryPeriod: Int?
    val justificationExplanation: String?
    val justificationTransactionalRelevance: String?
    val justificationBenefits: String?
    val justificationPilot: String?
    val risk: String?
    val documentation: String?
    val documentationExpectedImpacts: String?
    val ownershipSiteLocation: String?
    val ownershipRetain: String?
    val ownershipMaintenance: String?
    val country: String?
    val countryCode: String?
    val nutsRegion2: String?
    val nutsRegion2Code: String?
    val nutsRegion3: String?
    val nutsRegion3Code: String?
    val street: String?
    val houseNumber: String?
    val postalCode: String?
    val city: String?
}
