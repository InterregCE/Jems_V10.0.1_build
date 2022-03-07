package io.cloudflight.jems.api.project.dto.report.partner.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.LocalDate

data class ProjectPartnerReportIdentificationDTO(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val period: Int?,
    val summary: Set<InputTranslation>,
    val problemsAndDeviations: Set<InputTranslation>,
    val targetGroups: List<ProjectPartnerReportIdentificationTargetGroupDTO>,
)
