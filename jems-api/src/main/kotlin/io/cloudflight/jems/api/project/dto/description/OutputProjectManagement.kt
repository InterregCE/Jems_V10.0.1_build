package io.cloudflight.jems.api.project.dto.description

/**
 * C7
 */
data class OutputProjectManagement(
    val projectCoordination: String?,
    val projectQualityAssurance: String?,
    val projectCommunication: String?,
    val projectFinancialManagement: String?,
    val projectCooperationCriteria: InputProjectCooperationCriteria?,
    val projectHorizontalPrinciples: InputProjectHorizontalPrinciples?
)
