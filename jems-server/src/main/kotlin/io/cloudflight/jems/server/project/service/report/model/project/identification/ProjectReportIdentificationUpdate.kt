package io.cloudflight.jems.server.project.service.report.model.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportIdentificationUpdate(
    var targetGroups: List<Set<InputTranslation>> = emptyList(),
    val highlights: Set<InputTranslation>,
    val partnerProblems: Set<InputTranslation>,
    val deviations: Set<InputTranslation>,
) {
    fun getHighlightsAsMap() = highlights.associateBy({ it.language }, { it.translation })

    fun getPartnerProblemsAsMap() = partnerProblems.associateBy({ it.language }, { it.translation })

    fun getDeviationsAsMap() = deviations.associateBy({ it.language }, { it.translation })
}
