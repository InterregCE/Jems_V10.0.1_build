package io.cloudflight.jems.server.project.service.report.partner.identification.control

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import java.time.LocalDate

fun toModelObject(
    report: ProjectPartnerReport,
    programmeTitle: String?,
    projectTitle: Set<InputTranslation>?,
    startAndEndDate: Pair<LocalDate, LocalDate?>?,
    identification: ProjectPartnerReportIdentification,
    designatedController: ReportDesignatedController,
    reportVerification: ReportVerification
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
    reportLastResubmission = report.lastResubmission,
    reportControlEnd = report.controlEnd,
    controllerFormats = identification.controllerFormats,
    type = identification.type,
    designatedController = designatedController,
    reportVerification = reportVerification,
)
