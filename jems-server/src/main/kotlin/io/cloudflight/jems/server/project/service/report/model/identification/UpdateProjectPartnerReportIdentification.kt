package io.cloudflight.jems.server.project.service.report.model.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.time.LocalDate

data class UpdateProjectPartnerReportIdentification(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val period: Int?,
    val summary: Set<InputTranslation>,
    val problemsAndDeviations: Set<InputTranslation>,
    val targetGroups: List<Set<InputTranslation>> = emptyList(),
    val nextReportForecast: BigDecimal,
    val spendingDeviations: Set<InputTranslation>,
) {
    fun getSummaryAsMap() = summary.associateBy({ it.language }, { it.translation })

    fun getProblemsAndDeviationsAsMap() = problemsAndDeviations.associateBy({ it.language }, { it.translation })

    fun getSpendingDeviationsAsMap() = spendingDeviations.associateBy({ it.language }, { it.translation })

}
