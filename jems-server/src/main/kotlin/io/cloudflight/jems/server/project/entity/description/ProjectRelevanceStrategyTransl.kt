package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationUuId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 strategy lang table
 */
@Entity(name = "project_description_c2_relevance_strategy_transl")
data class ProjectRelevanceStrategyTransl(

    @EmbeddedId
    val translationId: TranslationUuId,

    @Column
    val specification: String? = null

)
