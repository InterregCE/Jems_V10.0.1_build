package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 synergy lang table
 */
@Entity(name = "project_description_c2_relevance_synergy_transl")
class ProjectRelevanceSynergyTransl(
    @EmbeddedId
    override val translationId: TranslationId<ProjectRelevanceSynergyEntity>,
    val synergy: String?,
    val specification: String?
) : TranslationEntity()
