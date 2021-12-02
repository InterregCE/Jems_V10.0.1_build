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
    //activity translaiton
    val activityTitle: String?
    val activityLanguage: SystemLanguage?
    val activityDescription: String?
    //deliverable
    val deliverableId: Long?
    val deliverableNumber: Int?
    val deliverableStartPeriod: Int?
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
    //output translation
    val outputTitle: String?
    val outputDescription: String?
    val outputLanguage: SystemLanguage?
    //investment
    val investmentId: Long?
    val investmentNumber: Int?
    val investmentCountry: String?
    val investmentNutsRegion2: String?
    val investmentNutsRegion3: String?
    val investmentStreet: String?
    val investmentHouseNumber: String?
    val investmentPostalCode: String?
    val investmentCity: String?
    //investment translation
    val investmentTitle: String?
    val justificationExplanation: String?
    val justificationTransactionalRelevance: String?
    val justificationBenefits: String?
    val justificationPilot: String?
    val investmentRisk: String?
    val investmentDocumentation: String?
    val ownershipSiteLocation: String?
    val ownershipRetain: String?
    val ownershipMaintenance: String?
    val investmentLanguage: SystemLanguage?
}
