package io.cloudflight.jems.server.project.service.auditAndControl.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class AuditControl(
    val id: Long,
    val number: Int,

    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,

    val status: AuditControlStatus,
    val controllingBody: ControllingBody,
    val controlType: AuditControlType,
    val startDate: ZonedDateTime?,
    val endDate: ZonedDateTime?,
    val finalReportDate: ZonedDateTime?,

    val totalControlledAmount: BigDecimal,
    var totalCorrectionsAmount: BigDecimal,

    val comment: String?
)
