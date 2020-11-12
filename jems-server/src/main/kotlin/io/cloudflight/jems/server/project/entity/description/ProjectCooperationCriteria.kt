package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectCooperationCriteria(

    @field:NotNull
    val projectJointDevelopment: Boolean = false,

    val projectJointDevelopmentDescription: String? = null,

    @field:NotNull
    val projectJointImplementation: Boolean = false,

    val projectJointImplementationDescription: String? = null,

    @field:NotNull
    val projectJointStaffing: Boolean = false,

    val projectJointStaffingDescription: String? = null,

    @field:NotNull
    val projectJointFinancing: Boolean = false,

    val projectJointFinancingDescription: String? = null

) {
    fun ifNotEmpty(): ProjectCooperationCriteria? {
        if (projectJointDevelopment
            || !projectJointDevelopmentDescription.isNullOrEmpty()
            || projectJointImplementation
            || !projectJointImplementationDescription.isNullOrEmpty()
            || projectJointStaffing
            || !projectJointStaffingDescription.isNullOrEmpty()
            || projectJointFinancing
            || !projectJointFinancingDescription.isNullOrEmpty()
        )
            return this
        return null
    }
}
