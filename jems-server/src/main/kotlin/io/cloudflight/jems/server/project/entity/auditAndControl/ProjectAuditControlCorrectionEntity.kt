package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_audit_correction")
class ProjectAuditControlCorrectionEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "audit_control_id")
    @field:NotNull
    val auditControlEntity: AuditControlEntity,

    @field:NotNull
    val orderNr: Int,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val status: CorrectionStatus,

    @field:NotNull
    val linkedToInvoice: Boolean
)
