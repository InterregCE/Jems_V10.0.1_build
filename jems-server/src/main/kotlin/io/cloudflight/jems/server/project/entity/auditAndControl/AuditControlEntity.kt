package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "audit_control")
class AuditControlEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @field:NotNull
    val project: ProjectEntity,

    @field:NotNull
    val number: Int,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: AuditControlStatus,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var controllingBody: ControllingBody,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var controlType: AuditControlType,

    var startDate: ZonedDateTime?,

    var endDate: ZonedDateTime?,

    var finalReportDate: ZonedDateTime?,

    @field:NotNull
    var totalControlledAmount: BigDecimal,

    var comment: String?
)
