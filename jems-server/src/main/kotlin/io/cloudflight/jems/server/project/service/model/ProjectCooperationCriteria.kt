package io.cloudflight.jems.server.project.service.model

data class ProjectCooperationCriteria(
    val projectJointDevelopment: Boolean = false,
    val projectJointImplementation: Boolean = false,
    val projectJointStaffing: Boolean = false,
    val projectJointFinancing: Boolean = false
)
