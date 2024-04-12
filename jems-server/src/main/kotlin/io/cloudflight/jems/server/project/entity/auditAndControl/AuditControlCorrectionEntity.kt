package io.cloudflight.jems.server.project.entity.auditAndControl

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity(name = "audit_control_correction")
class AuditControlCorrectionEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @field:NotNull
    val auditControl: AuditControlEntity,

    @field:NotNull
    val orderNr: Int,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: AuditControlStatus,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val correctionType: AuditControlCorrectionType,


    @ManyToOne
    var followUpOfCorrection: AuditControlCorrectionEntity?,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var followUpOfCorrectionType: CorrectionFollowUpType,

    var repaymentDate: LocalDate?,

    var lateRepayment: LocalDate?,


    @ManyToOne(optional = true)
    var partnerReport: ProjectPartnerReportEntity?,

    @ManyToOne(optional = true)
    @JoinColumns(
        JoinColumn(name = "project_lump_sum_id", referencedColumnName = "project_id"),
        JoinColumn(name = "project_lump_sum_order_nr", referencedColumnName = "order_nr")
    )
    var lumpSum: ProjectLumpSumEntity?,

    var lumpSumPartnerId: Long?,

    @ManyToOne
    var programmeFund: ProgrammeFundEntity?,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var impact: AuditControlCorrectionImpactAction,

    @field:NotNull
    @field:Size(max = 2000)
    var impactComment: String,

    @ManyToOne
    var expenditure: PartnerReportExpenditureCostEntity?,

    @Enumerated(EnumType.STRING)
    var costCategory: BudgetCostCategory?,

    var procurementId: Long?,

    var projectModificationId: Long?,
)
