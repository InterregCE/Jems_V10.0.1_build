package io.cloudflight.jems.api.project.dto.description

/**
 * C7.5
 */
data class InputProjectCooperationCriteria (
    val projectJointDevelopment: Boolean = false,
    val projectJointDevelopmentDescription: String? = null,
    val projectJointImplementation: Boolean = false,
    val projectJointImplementationDescription: String? = null,
    val projectJointStaffing: Boolean = false,
    val projectJointStaffingDescription: String? = null,
    val projectJointFinancing: Boolean = false,
    val projectJointFinancingDescription: String? = null
)
