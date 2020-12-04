package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationUuId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 synergy lang table
 */
@Entity(name = "project_description_c2_relevance_synergy_transl")
data class ProjectRelevanceSynergyTransl(

    @EmbeddedId
    val translationId: TranslationUuId,

    @Column
    val synergy: String?,

    @Column
    val specification: String?

)
