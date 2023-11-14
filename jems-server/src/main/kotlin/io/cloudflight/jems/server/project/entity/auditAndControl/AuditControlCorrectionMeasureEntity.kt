package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "audit_control_correction_measure")
class AuditControlCorrectionMeasureEntity(

    @Id
    val correctionId: Long = 0,

    @ManyToOne(optional = false)
    @MapsId
    @field:NotNull
    val correction: AuditControlCorrectionEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var scenario: ProjectCorrectionProgrammeMeasureScenario,

    var comment: String? = null,

)
