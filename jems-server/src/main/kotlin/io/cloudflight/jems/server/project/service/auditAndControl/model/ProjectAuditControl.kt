package io.cloudflight.jems.server.project.service.auditAndControl.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProjectAuditControl(
    val id: Long,
    val number: Int,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val status: AuditStatus,
    val controllingBody: ControllingBody,
    val controlType: AuditControlType,
    val startDate: ZonedDateTime?,
    val endDate: ZonedDateTime?,
    val finalReportDate: ZonedDateTime?,

    val totalControlledAmount: BigDecimal,
    val totalCorrectionsAmount: BigDecimal,

    val comment: String?
)
