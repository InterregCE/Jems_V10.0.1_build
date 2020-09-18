package io.cloudflight.ems.project.entity.description

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * C1
 */
@Entity(name = "project_description_c1_overall_objective")
data class ProjectOverallObjective(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Column
    val projectOverallObjective: String?

)
