package io.cloudflight.jems.server.project.entity.auditAndControl.temporaryModels

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class AuditControlCorrectionBulkTmpObject(
    val projectId: Long,
    val projectIdentifier: String,
    val projectAcronym: String,
    val programmePriority: ProgrammeObjective?,
    val programmePriorityCode: String?,
    val programmePriorityPolicy: ProgrammeObjectivePolicy?,
    val programmePriorityPolicyCode: String?,
    val typologyProv94: ContractingMonitoringExtendedOption?,
    val typologyProv95: ContractingMonitoringExtendedOption?,

    val acNumber: Int,
    val acStatus: AuditControlStatus,
    val acControllingBody: ControllingBody,
    val acControlType: AuditControlType,
    val acStartDate: ZonedDateTime?,
    val acEndDate: ZonedDateTime?,
    val acFinalReportDate: ZonedDateTime?,
    val acTotalControlledAmount: BigDecimal,
    val acComment: String?,

    val accNumber: Int,
    val accStatus: AuditControlStatus,
    val accType: AuditControlCorrectionType,
    val accFollowUpNumber: Int?,
    val accFollowUpOfCorrectionType: CorrectionFollowUpType,
    val accRepaymentDate: LocalDate?,
    val accLateRepayment: LocalDate?,
    val partnerRole: ProjectPartnerRole?,
    val partnerNumber: Int?,
    val partnerAbbreviationFromReport: String?,
    val partnerAbbreviationFromPartner: String?,
    val reportProjectNumber: Int?,
    val reportPartnerNumber: Int?,
    val fundId: Long?,
    val fundType: ProgrammeFundType?,
    val costCategoryWhenExpenditure: ReportBudgetCategory?,
    val costCategoryWhenLumpSum: BudgetCostCategory?,
    val procurementContractName: String?,

    val financeDeduction: Boolean,
    val financeFundAmount: BigDecimal,
    val financePublicContribution: BigDecimal,
    val financeAutoPublicContribution: BigDecimal,
    val financePrivateContribution: BigDecimal,
    val financeInfoSentBeneficiaryDate: LocalDate?,
    val financeInfoSentBeneficiaryComment: String?,
    val financeCorrectionType: CorrectionType?,
    val financeClericalTechnicalMistake: Boolean,
    val financeGoldPlating: Boolean,
    val financeSuspectedFraud: Boolean,
    val financeCorrectionComment: String?,

    val impact: AuditControlCorrectionImpactAction,
    val impactComment: String,
    val impactModificationStatusId: Long?,

    val measureEcPaymentId: Long?,
    val measureYearId: Long?,
    val measureYearStartDate: LocalDate?,
    val measureYearEndDate: LocalDate?,
    val measureScenario: ProjectCorrectionProgrammeMeasureScenario,
    val measureComment: String?,
    val measureIncludedInEcPaymentId: Long?,
    val measureIncludedInEcPaymentYearId: Long?,
    val measureIncludedInEcPaymentYearStartDate: LocalDate?,
    val measureIncludedInEcPaymentYearEndDate: LocalDate?,
    val measureIncludedInAccountYearId: Long?,
    val measureIncludedInAccountYearStartDate: LocalDate?,
    val measureIncludedInAccountYearEndDate: LocalDate?,

    val includedTotal: BigDecimal?,
    val includedTotalWithoutArt9495: BigDecimal?,
    val includedUnion: BigDecimal?,
    val includedFundAmount: BigDecimal?,
    val includedPrivateContribution: BigDecimal?,
    val includedOfWhichPublic: BigDecimal?,
    val includedOfWhichAutoPublic: BigDecimal?,
    val includedOfWhichPrivate: BigDecimal?,
    val includedComment: String?,
)
