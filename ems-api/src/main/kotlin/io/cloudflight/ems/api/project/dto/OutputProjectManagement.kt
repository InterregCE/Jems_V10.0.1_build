package io.cloudflight.ems.api.project.dto

data class OutputProjectManagement(
    val projectCoordination: String?,
    val projectQualityAssurance: String?,
    val projectCommunication: String?,
    val projectFinancialManagement: String?,
    val projectJointDevelopment: String?,
    val projectJointImplementation: String?,
    val projectJointStaffing: String?,
    val projectJointFinancing: String?,
    val projectHorizontalPrinciples: InputProjectHorizontalPrinciples?
)
