package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_audit_control")
class AuditControlEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val projectId: Long,

    @field:NotNull
    val projectCustomIdentifier: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: AuditStatus,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var controllingBody: ControllingBody,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var controlType: AuditControlType,

    @field:NotNull
    var startDate: ZonedDateTime,

    @field:NotNull
    var endDate: ZonedDateTime,

    var finalReportDate: ZonedDateTime?,

    @field:NotNull
    var totalControlledAmount: BigDecimal,

    @field:NotNull
    var totalCorrectionsAmount: BigDecimal,

    var comment: String?

)