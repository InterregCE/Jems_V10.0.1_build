package io.cloudflight.jems.api.project.dto.description

/**
 * C7.5
 */
data class InputProjectCooperationCriteria (
    val projectJointDevelopment: Boolean = false,
    val projectJointImplementation: Boolean = false,
    val projectJointStaffing: Boolean = false,
    val projectJointFinancing: Boolean = false
)
