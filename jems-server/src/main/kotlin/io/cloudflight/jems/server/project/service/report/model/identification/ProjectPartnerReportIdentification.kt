package io.cloudflight.jems.server.project.service.report.model.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportType
import java.time.LocalDate

data class ProjectPartnerReportIdentification(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val summary: Set<InputTranslation>,
    val problemsAndDeviations: Set<InputTranslation>,
    val spendingDeviations: Set<InputTranslation>,
    val targetGroups: List<ProjectPartnerReportIdentificationTargetGroup>,
    val spendingProfile: ProjectPartnerReportSpendingProfile,
    val controllerFormats: Set<ReportFileFormat>,
    val type: ReportType,
)
