package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * C2 lang table
 */
@Entity(name = "project_description_c2_relevance_transl")
data class ProjectRelevanceTransl(

    @EmbeddedId
    val translationId: TranslationId,

    // C2.1
    @Column
    val territorialChallenge: String? = null,

    // C2.2
    @Column
    val commonChallenge: String? = null,

    // C2.3
    @Column
    val transnationalCooperation: String? = null,

    // C2.7
    @Column
    val availableKnowledge: String? = null

)
