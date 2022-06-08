package io.cloudflight.jems.server.project.service.report.model.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.LocalDate

data class UpdateProjectPartnerReportIdentification(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val period: Int?,
    val summary: Set<InputTranslation>,
    val problemsAndDeviations: Set<InputTranslation>,
    val targetGroups: List<Set<InputTranslation>> = emptyList(),
) {
    fun getSummaryAsMap() = summary.associateBy({ it.language }, { it.translation })

    fun getProblemsAndDeviationsAsMap() = problemsAndDeviations.associateBy({ it.language }, { it.translation })
}
