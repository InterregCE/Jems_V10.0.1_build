package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "project_audit_correction_identification")
class ProjectCorrectionIdentificationEntity (

    @Id
    val correctionId: Long = 0,

    @MapsId
    @ManyToOne
    @JoinColumn(name = "correction_id")
    @field:NotNull
    val correctionEntity: ProjectAuditControlCorrectionEntity,

    var followUpOfCorrectionId: Long?,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var correctionFollowUpType: CorrectionFollowUpType,

    var repaymentFrom: ZonedDateTime?,

    var lateRepaymentTo: ZonedDateTime?,

    var partnerId: Long?,

    var partnerReportId: Long?,

    var programmeFundId: Long?
)
