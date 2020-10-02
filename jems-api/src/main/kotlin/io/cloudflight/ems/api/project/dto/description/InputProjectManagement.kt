package io.cloudflight.ems.api.project.dto.description

/**
 * C7
 */
data class InputProjectManagement(
    val projectCoordination: String?,   // C7.1
    val projectQualityAssurance: String?,   // C7.2
    val projectCommunication: String?,  // C7.3
    val projectFinancialManagement: String?,    // C7.4
    val projectCooperationCriteria: InputProjectCooperationCriteria?, // C7.5
    val projectHorizontalPrinciples: InputProjectHorizontalPrinciples?  // C7.6
)
