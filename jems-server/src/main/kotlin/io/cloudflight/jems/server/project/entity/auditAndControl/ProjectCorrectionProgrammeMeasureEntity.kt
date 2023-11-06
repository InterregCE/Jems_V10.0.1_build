package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name ="project_audit_correction_programme_measure")
class ProjectCorrectionProgrammeMeasureEntity(

    @Id
    val correctionId: Long = 0,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correction_id")
    @field:NotNull
    val correctionEntity: ProjectAuditControlCorrectionEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var scenario: ProjectCorrectionProgrammeMeasureScenario,

    var comment: String? = null,

)
