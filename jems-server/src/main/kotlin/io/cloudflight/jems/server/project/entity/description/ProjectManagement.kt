package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id

/**
 * C7
 */
@Entity(name = "project_description_c7_management")
data class ProjectManagement(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Column
    val projectCoordination: String?,

    @Column
    val projectQualityAssurance: String?,

    @Column
    val projectCommunication: String?,

    @Column
    val projectFinancialManagement: String?,

    @Embedded
    val projectCooperationCriteria: ProjectCooperationCriteria?,

    @Embedded
    val projectHorizontalPrinciples: ProjectHorizontalPrinciples?

)
