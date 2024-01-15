package io.cloudflight.jems.server.project.service.auditAndControl.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class AuditControlUpdate(
    val controllingBody: ControllingBody,
    val controlType: AuditControlType,
    val startDate: ZonedDateTime?,
    val endDate: ZonedDateTime?,
    val finalReportDate: ZonedDateTime?,
    val totalControlledAmount: BigDecimal,
    val comment: String?,
)
