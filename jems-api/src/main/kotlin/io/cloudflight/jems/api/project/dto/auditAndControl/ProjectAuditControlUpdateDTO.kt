package io.cloudflight.jems.api.project.dto.auditAndControl

import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProjectAuditControlUpdateDTO(
    val controllingBody: ControllingBodyDTO,
    val controlType: AuditControlTypeDTO,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val finalReportDate: ZonedDateTime?,
    val totalControlledAmount: BigDecimal,
    val comment: String?
)
