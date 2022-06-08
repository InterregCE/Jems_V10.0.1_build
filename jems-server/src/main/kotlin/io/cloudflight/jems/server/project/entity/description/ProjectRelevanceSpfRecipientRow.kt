package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectRelevanceSpfRecipientRow: TranslationView {
    val id: String
    val projectId: Long
    val recipientGroup: ProjectTargetGroupDTO
    val specification: String?

}
