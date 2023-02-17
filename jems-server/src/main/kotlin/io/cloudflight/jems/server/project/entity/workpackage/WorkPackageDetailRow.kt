package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface WorkPackageDetailRow : TranslationView {
    //from workpackage
    val id: Long
    val number: Int
    val name: String?
    //workpackage translation
    val specificObjective: String?
    val objectiveAndAudience: String?
    //activity
    val activityId: Long?
    val activityNumber: Int?
    val startPeriod: Int?
    val endPeriod: Int?
    val partnerId: Long?
    val activityDeactivated: Boolean?
    //activity translation
    val activityTitle: String?
    val activityLanguage: SystemLanguage?
    val activityDescription: String?
    //deliverable
    val deliverableId: Long?
    val deliverableNumber: Int?
    val deliverableStartPeriod: Int?
    val deliverableDeactivated: Boolean?
    //deliverable translation
    val deliverableDescription: String?
    val deliverableTitle: String?
    val deliverableLanguage: SystemLanguage?
    //output
    val outputNumber: Int?
    val programmeOutputIndicatorId: Long?
    val programmeOutputIndicatorIdentifier: String?
    val programmeOutputIndicatorLanguage: SystemLanguage?
    val programmeOutputIndicatorName: String?
    val programmeOutputIndicatorMeasurementUnit: String?
    val targetValue: BigDecimal?
    val outputPeriodNumber: Int?
    val outputDeactivated: Boolean?
    //output translation
    val outputTitle: String?
    val outputDescription: String?
    val outputLanguage: SystemLanguage?
    //investment
    val investmentId: Long?
    val investmentNumber: Int?
    val investmentCountry: String?
    val investmentCountryCode: String?
    val investmentNutsRegion2: String?
    val investmentNutsRegion2Code: String?
    val investmentNutsRegion3: String?
    val investmentNutsRegion3Code: String?
    val investmentStreet: String?
    val investmentHouseNumber: String?
    val investmentPostalCode: String?
    val investmentCity: String?
    val investmentExpectedDeliveryPeriod: Int?
    val investmentDeactivated: Boolean?
    //investment translation
    val investmentTitle: String?
    val justificationExplanation: String?
    val justificationTransactionalRelevance: String?
    val justificationBenefits: String?
    val justificationPilot: String?
    val investmentRisk: String?
    val investmentDocumentation: String?
    val investmentDocumentationExpectedImpacts: String?
    val ownershipSiteLocation: String?
    val ownershipRetain: String?
    val ownershipMaintenance: String?
    val investmentLanguage: SystemLanguage?
}
