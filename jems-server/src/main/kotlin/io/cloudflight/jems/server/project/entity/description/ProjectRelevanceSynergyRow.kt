package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectRelevanceSynergyRow: TranslationView {
    val id: String
    val projectId: Long

    // _relevance_synergy_transl
    val synergy: String?
    val specification: String?

}
