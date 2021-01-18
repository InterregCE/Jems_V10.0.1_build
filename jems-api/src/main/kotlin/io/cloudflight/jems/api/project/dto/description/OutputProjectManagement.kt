package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

/**
 * C7
 */
data class OutputProjectManagement(
    val projectCoordination: Set<InputTranslation>? = emptySet(),
    val projectQualityAssurance: Set<InputTranslation>? = emptySet(),
    val projectCommunication: Set<InputTranslation>? = emptySet(),
    val projectFinancialManagement: Set<InputTranslation>? = emptySet(),
    val projectCooperationCriteria: InputProjectCooperationCriteria?,
    val projectJointDevelopmentDescription: Set<InputTranslation>? = emptySet(),
    val projectJointImplementationDescription: Set<InputTranslation>? = emptySet(),
    val projectJointStaffingDescription: Set<InputTranslation>? = emptySet(),
    val projectJointFinancingDescription: Set<InputTranslation>? = emptySet(),
    val projectHorizontalPrinciples: InputProjectHorizontalPrinciples?,
    val sustainableDevelopmentDescription: Set<InputTranslation>? = emptySet(),
    val equalOpportunitiesDescription: Set<InputTranslation>? = emptySet(),
    val sexualEqualityDescription: Set<InputTranslation>? = emptySet()
)
