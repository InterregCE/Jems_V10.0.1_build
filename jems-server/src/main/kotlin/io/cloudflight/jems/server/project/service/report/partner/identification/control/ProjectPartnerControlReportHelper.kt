package io.cloudflight.jems.server.project.service.report.partner.identification.control

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReport
import java.time.LocalDate

fun toModelObject(
    report: ProjectPartnerReport,
    programmeTitle: String?,
    projectTitle: Set<InputTranslation>?,
    startAndEndDate: Pair<LocalDate, LocalDate?>?,
    identification: ProjectPartnerReportIdentification,
) = ProjectPartnerControlReport(
    id = report.id,
    programmeTitle = programmeTitle ?: "",
    projectTitle = projectTitle ?: emptySet(),
    projectAcronym = report.identification.projectAcronym,
    projectIdentifier = report.identification.projectIdentifier,
    linkedFormVersion = report.version,
    reportNumber = report.reportNumber,
    projectStart = startAndEndDate?.first,
    projectEnd = startAndEndDate?.second,
    reportPeriodNumber = identification.spendingProfile.periodDetail?.number,
    reportPeriodStart = identification.startDate,
    reportPeriodEnd = identification.endDate,
    reportFirstSubmission = report.firstSubmission!!,
    controllerFormats = identification.controllerFormats,
    type = identification.type,
)
