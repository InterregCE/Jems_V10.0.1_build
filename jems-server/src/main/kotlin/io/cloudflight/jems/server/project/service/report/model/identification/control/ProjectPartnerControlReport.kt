package io.cloudflight.jems.server.project.service.report.model.identification.control

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectPartnerControlReport(
    val id: Long,
    val programmeTitle: String,
    val projectTitle: Set<InputTranslation>,
    val projectAcronym: String,
    val projectIdentifier: String,
    val linkedFormVersion: String,
    val reportNumber: Int,
    val projectStart: LocalDate?,
    val projectEnd: LocalDate?,
    val reportPeriodNumber: Int?,
    val reportPeriodStart: LocalDate?,
    val reportPeriodEnd: LocalDate?,
    val reportFirstSubmission: ZonedDateTime,
    val controllerFormats: Set<ReportFileFormat>,
    val type: ReportType,
)
