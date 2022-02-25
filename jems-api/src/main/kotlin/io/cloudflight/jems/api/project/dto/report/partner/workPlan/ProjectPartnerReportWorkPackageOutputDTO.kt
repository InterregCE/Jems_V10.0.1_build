package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportWorkPackageOutputDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val contribution: Boolean?,
    val evidence: Boolean?,
)
