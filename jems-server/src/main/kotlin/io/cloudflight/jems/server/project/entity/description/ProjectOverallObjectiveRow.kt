package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectOverallObjectiveRow: TranslationView {
    val projectId: Long

    // _overall_objective_transl
    val overallObjective: String?

}
