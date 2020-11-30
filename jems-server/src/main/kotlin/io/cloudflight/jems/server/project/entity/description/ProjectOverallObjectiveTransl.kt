package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C1 overall objective lang table
 */
@Entity(name = "project_description_c1_overall_objective_transl")
data class ProjectOverallObjectiveTransl(

    @EmbeddedId
    val translationId: TranslationId,

    @Column
    val overallObjective: String? = null

)
