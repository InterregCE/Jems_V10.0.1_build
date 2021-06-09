package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectManagement(
    val projectCoordination: Set<InputTranslation>? = emptySet(),
    val projectQualityAssurance: Set<InputTranslation>? = emptySet(),
    val projectCommunication: Set<InputTranslation>? = emptySet(),
    val projectFinancialManagement: Set<InputTranslation>? = emptySet(),
    val projectCooperationCriteria: ProjectCooperationCriteria?,
    val projectJointDevelopmentDescription: Set<InputTranslation>? = emptySet(),
    val projectJointImplementationDescription: Set<InputTranslation>? = emptySet(),
    val projectJointStaffingDescription: Set<InputTranslation>? = emptySet(),
    val projectJointFinancingDescription: Set<InputTranslation>? = emptySet(),
    val projectHorizontalPrinciples: ProjectHorizontalPrinciples?,
    val sustainableDevelopmentDescription: Set<InputTranslation>? = emptySet(),
    val equalOpportunitiesDescription: Set<InputTranslation>? = emptySet(),
    val sexualEqualityDescription: Set<InputTranslation>? = emptySet()
)
