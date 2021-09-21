package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectCooperationCriteriaEntity(

    @field:NotNull
    val projectJointDevelopment: Boolean = false,

    @field:NotNull
    val projectJointImplementation: Boolean = false,

    @field:NotNull
    val projectJointStaffing: Boolean = false,

    @field:NotNull
    val projectJointFinancing: Boolean = false

) {
    fun ifNotEmpty(): ProjectCooperationCriteriaEntity? {
        if (projectJointDevelopment
            || projectJointImplementation
            || projectJointStaffing
            || projectJointFinancing
        )
            return this
        return null
    }
}
