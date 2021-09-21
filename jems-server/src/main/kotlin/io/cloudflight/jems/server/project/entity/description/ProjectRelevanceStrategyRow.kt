package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectRelevanceStrategyRow: TranslationView {
    val id: String
    val projectId: Long
    val strategy: ProgrammeStrategy?

    // _relevance_strategy_transl
    val specification: String?

}
