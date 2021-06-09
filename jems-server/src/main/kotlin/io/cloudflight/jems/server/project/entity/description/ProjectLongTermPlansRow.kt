package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectLongTermPlansRow: TranslationView {
    val projectId: Long

    // _long_term_plans_transl
    val projectOwnership: String?
    val projectDurability: String?
    val projectTransferability: String?
}
