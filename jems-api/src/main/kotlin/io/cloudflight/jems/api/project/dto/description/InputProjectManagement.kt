package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

/**
 * C7
 */
data class InputProjectManagement(
    val projectCoordination: Set<InputTranslation> = emptySet(),   // C7.1
    val projectQualityAssurance: Set<InputTranslation> = emptySet(),   // C7.2
    val projectCommunication: Set<InputTranslation> = emptySet(),  // C7.3
    val projectFinancialManagement: Set<InputTranslation> = emptySet(),    // C7.4
    val projectCooperationCriteria: InputProjectCooperationCriteria?, // C7.5
    val projectJointDevelopmentDescription: Set<InputTranslation> = emptySet(),
    val projectJointImplementationDescription: Set<InputTranslation> = emptySet(),
    val projectJointStaffingDescription: Set<InputTranslation> = emptySet(),
    val projectJointFinancingDescription: Set<InputTranslation> = emptySet(),
    val projectHorizontalPrinciples: InputProjectHorizontalPrinciples?,  // C7.6
    val sustainableDevelopmentDescription: Set<InputTranslation> = emptySet(),
    val equalOpportunitiesDescription: Set<InputTranslation> = emptySet(),
    val sexualEqualityDescription: Set<InputTranslation> = emptySet()
)
