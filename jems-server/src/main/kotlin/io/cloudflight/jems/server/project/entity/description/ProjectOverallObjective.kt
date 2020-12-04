package io.cloudflight.jems.server.project.entity.description

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * C1
 */
@Entity(name = "project_description_c1_overall_objective")
data class ProjectOverallObjective(

    @Id
    val projectId: Long,

    // projectOverallObjective
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.projectId")
    val translatedValues: Set<ProjectOverallObjectiveTransl> = emptySet()

)
