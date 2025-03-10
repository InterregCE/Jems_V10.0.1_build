package io.cloudflight.jems.api.project.dto.auditAndControl

import java.math.BigDecimal
import java.time.ZonedDateTime

data class AuditControlDTO(
    val id: Long,
    val number: Int,

    val projectId: Long,
    val projectCustomIdentifier: String,

    val status: AuditStatusDTO,
    val controllingBody: ControllingBodyDTO,
    val controlType: AuditControlTypeDTO,
    val startDate: ZonedDateTime?,
    val endDate: ZonedDateTime?,
    val finalReportDate: ZonedDateTime?,

    val totalControlledAmount: BigDecimal,
    val totalCorrectionsAmount: BigDecimal,
    val existsOngoing: Boolean,
    val existsClosed: Boolean,

    val comment: String?
)
