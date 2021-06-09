package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectRelevanceRow: TranslationView {
    val projectId: Long

    // _relevance_transl
    val territorialChallenge: String?
    val commonChallenge: String?
    val transnationalCooperation: String?
    val availableKnowledge: String?

}
