package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 strategy lang table
 */
@Entity(name = "project_description_c2_relevance_strategy_transl")
class ProjectRelevanceStrategyTransl(
    @EmbeddedId
    override val translationId: TranslationId<ProjectRelevanceStrategyEntity>,
    val specification: String? = null
) : TranslationEntity()
