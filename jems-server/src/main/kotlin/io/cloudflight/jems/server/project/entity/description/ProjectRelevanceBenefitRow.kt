package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectRelevanceBenefitRow: TranslationView {
    val id: String
    val projectId: Long
    val targetGroup: ProjectTargetGroupDTO

    // _relevance_benefit_transl
    val specification: String?

}
