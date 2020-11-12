package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

/**
 * C7
 */
@Entity(name = "project_description_c7_management")
data class ProjectManagement(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    val projectCoordination: String?,

    val projectQualityAssurance: String?,

    val projectCommunication: String?,

    val projectFinancialManagement: String?,

    @Embedded
    val projectCooperationCriteria: ProjectCooperationCriteria?,

    @Embedded
    val projectHorizontalPrinciples: ProjectHorizontalPrinciples?

)
