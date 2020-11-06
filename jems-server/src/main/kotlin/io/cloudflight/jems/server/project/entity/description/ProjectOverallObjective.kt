package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Entity
import javax.persistence.Id

/**
 * C1
 */
@Entity(name = "project_description_c1_overall_objective")
data class ProjectOverallObjective(

    @Id
    val projectId: Long,

    val projectOverallObjective: String?

)
