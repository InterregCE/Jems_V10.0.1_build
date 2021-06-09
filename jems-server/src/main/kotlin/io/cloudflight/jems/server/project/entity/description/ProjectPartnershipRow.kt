package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectPartnershipRow: TranslationView {
    val projectId: Long

    // _partnership_transl
    val projectPartnership: String?

}
