package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.validation.constraints.NotNull

@Entity(name = "audit_control_correction_finance")
class AuditControlCorrectionFinanceEntity (

    @Id
    val correctionId: Long = 0,

    @ManyToOne(optional = false)
    @MapsId
    @field:NotNull
    val correction: AuditControlCorrectionEntity,

    @field:NotNull
    var deduction: Boolean,

    @field:NotNull
    var fundAmount: BigDecimal,

    @field:NotNull
    var publicContribution: BigDecimal,

    @field:NotNull
    var autoPublicContribution: BigDecimal,

    @field:NotNull
    var privateContribution: BigDecimal,

    var infoSentBeneficiaryDate: LocalDate?,

    var infoSentBeneficiaryComment: String?,

    @Enumerated(EnumType.STRING)
    var correctionType: CorrectionType?,

    @field:NotNull
    var clericalTechnicalMistake: Boolean,

    @field:NotNull
    var goldPlating: Boolean,

    @field:NotNull
    var suspectedFraud: Boolean,

    var correctionComment: String?,
)
