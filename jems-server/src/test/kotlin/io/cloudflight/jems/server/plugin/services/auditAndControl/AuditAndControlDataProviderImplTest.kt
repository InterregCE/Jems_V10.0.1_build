package io.cloudflight.jems.server.plugin.services.auditAndControl

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.plugin.contract.models.payments.export.AuditControlCorrectionTypeData
import io.cloudflight.jems.plugin.contract.models.payments.export.AuditControlStatusData
import io.cloudflight.jems.plugin.contract.models.payments.export.ControllingBodyData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeObjectivePolicyData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionBulkObjectData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlTypeData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionFollowUpTypeData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionImpactActionData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionTypeData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionProgrammeMeasureScenarioData
import io.cloudflight.jems.plugin.contract.models.project.budget.BudgetCostCategoryData
import io.cloudflight.jems.plugin.contract.models.project.contracting.monitoring.ContractingMonitoringExtendedOptionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ReportBudgetCategoryData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.auditAndControl.temporaryModels.AuditControlCorrectionBulkTmpObject
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection.AuditControlCorrectionPagingService
import io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.AuditControlCorrectionMeasurePersistence
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
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.stream.Stream

internal class AuditAndControlDataProviderImplTest : UnitTest() {

    val date1 = ZonedDateTime.now().plusDays(1)
    val date2 = ZonedDateTime.now().plusDays(2)
    val date3 = ZonedDateTime.now().plusDays(3)
    val date4 = LocalDate.now().plusDays(4)
    val date5 = LocalDate.now().plusDays(5)
    val date6 = LocalDate.now().plusDays(6)
    val date7 = LocalDate.now().plusDays(7)
    val date8 = LocalDate.now().plusDays(8)

    private val correctionFetched = AuditControlCorrectionBulkTmpObject(
        projectId = 45L,
        projectIdentifier = "PRO_45",
        projectAcronym = "acr 45 ou yeah",
        programmePriority = ProgrammeObjectivePolicy.AdvancedTechnologies.objective,
        programmePriorityCode = "obj code",
        programmePriorityPolicy = ProgrammeObjectivePolicy.AdvancedTechnologies,
        programmePriorityPolicyCode = "policy code",
        typologyProv94 = ContractingMonitoringExtendedOption.Yes,
        typologyProv95 = ContractingMonitoringExtendedOption.Partly,
        acNumber = 14,
        acStatus = AuditControlStatus.Ongoing,
        acControllingBody = ControllingBody.OLAF,
        acControlType = AuditControlType.Administrative,
        acStartDate = date1,
        acEndDate = date2,
        acFinalReportDate = date3,
        acTotalControlledAmount = BigDecimal.valueOf(4578L, 2),
        acComment = "ac comment",
        accNumber = 478,
        accStatus = AuditControlStatus.Closed,
        accType = AuditControlCorrectionType.LinkedToCostOption,
        accFollowUpNumber = 7,
        accFollowUpOfCorrectionType = CorrectionFollowUpType.Interest,
        accRepaymentDate = date4,
        accLateRepayment = date5,
        partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        partnerNumber = 45,
        partnerAbbreviationFromReport = "abbr from report",
        partnerAbbreviationFromPartner = "abbr from partner",
        reportProjectNumber = 23,
        reportPartnerNumber = 64,
        fundId = 4L,
        fundType = ProgrammeFundType.NDICI,
        costCategoryWhenExpenditure = ReportBudgetCategory.ExternalCosts,
        costCategoryWhenLumpSum = BudgetCostCategory.SpfCost,
        procurementContractName = "proc",
        financeDeduction = true,
        financeFundAmount = BigDecimal.valueOf(648L, 2),
        financePublicContribution = BigDecimal.valueOf(485L, 2),
        financeAutoPublicContribution = BigDecimal.valueOf(548L, 2),
        financePrivateContribution = BigDecimal.valueOf(336L, 2),
        financeInfoSentBeneficiaryDate = date6,
        financeInfoSentBeneficiaryComment = "beneficiary",
        financeCorrectionType = CorrectionType.Ref11Dot1,
        financeClericalTechnicalMistake = true,
        financeGoldPlating = false,
        financeSuspectedFraud = true,
        financeCorrectionComment = "correction comm",
        impact = AuditControlCorrectionImpactAction.RepaymentByProject,
        impactComment = "repay comm",
        impactModificationStatusId = 6998L,
        measureEcPaymentId = 75L,
        measureYearId = 74L,
        measureYearStartDate = date7,
        measureYearEndDate = date8,
        measureScenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
        measureComment = "measure comm",
        measureIncludedInEcPaymentId = 62L,
        measureIncludedInEcPaymentYearId = 63L,
        measureIncludedInEcPaymentYearStartDate = date7,
        measureIncludedInEcPaymentYearEndDate = date8,
        measureIncludedInAccountYearId = 54L,
        measureIncludedInAccountYearStartDate = date5,
        measureIncludedInAccountYearEndDate = date6,
        includedTotal = BigDecimal.valueOf(654L, 2),
        includedTotalWithoutArt9495 = BigDecimal.valueOf(655L, 2),
        includedUnion = BigDecimal.valueOf(656L, 2),
        includedFundAmount = BigDecimal.valueOf(657L, 2),
        includedPrivateContribution = BigDecimal.valueOf(658L, 2),
        includedOfWhichPublic = BigDecimal.valueOf(659L, 2),
        includedOfWhichAutoPublic = BigDecimal.valueOf(660L, 2),
        includedOfWhichPrivate = BigDecimal.valueOf(661L, 2),
        includedComment = "included comment",
    )

    private val correctionExpected = AuditControlCorrectionBulkObjectData(
        projectId = 45L,
        projectIdentifier = "PRO_45",
        projectAcronym = "acr 45 ou yeah",
        programmePriority = ProgrammeObjectivePolicyData.AdvancedTechnologies.objective,
        programmePriorityCode = "obj code",
        programmePriorityPolicy = ProgrammeObjectivePolicyData.AdvancedTechnologies,
        programmePriorityPolicyCode = "policy code",
        typologyProv94 = ContractingMonitoringExtendedOptionData.Yes,
        typologyProv95 = ContractingMonitoringExtendedOptionData.Partly,
        acNumber = 14,
        acStatus = AuditControlStatusData.Ongoing,
        acControllingBody = ControllingBodyData.OLAF,
        acControlType = AuditControlTypeData.Administrative,
        acStartDate = date1,
        acEndDate = date2,
        acFinalReportDate = date3,
        acTotalControlledAmount = BigDecimal.valueOf(4578L, 2),
        acComment = "ac comment",
        accNumber = 478,
        accStatus = AuditControlStatusData.Closed,
        accType = AuditControlCorrectionTypeData.LinkedToCostOption,
        accFollowUpNumber = 7,
        accFollowUpOfCorrectionType = CorrectionFollowUpTypeData.Interest,
        accRepaymentDate = date4,
        accLateRepayment = date5,
        partnerRole = ProjectPartnerRoleData.LEAD_PARTNER,
        partnerNumber = 45,
        partnerAbbreviationFromReport = "abbr from report",
        partnerAbbreviationFromPartner = "abbr from partner",
        reportProjectNumber = 23,
        reportPartnerNumber = 64,
        fundId = 4L,
        fundType = ProgrammeFundTypeData.NDICI,
        costCategoryWhenExpenditure = ReportBudgetCategoryData.ExternalCosts,
        costCategoryWhenLumpSum = BudgetCostCategoryData.SpfCost,
        procurementContractName = "proc",
        financeDeduction = true,
        financeFundAmount = BigDecimal.valueOf(648L, 2),
        financePublicContribution = BigDecimal.valueOf(485L, 2),
        financeAutoPublicContribution = BigDecimal.valueOf(548L, 2),
        financePrivateContribution = BigDecimal.valueOf(336L, 2),
        financeInfoSentBeneficiaryDate = date6,
        financeInfoSentBeneficiaryComment = "beneficiary",
        financeCorrectionType = CorrectionTypeData.Ref11Dot1,
        financeClericalTechnicalMistake = true,
        financeGoldPlating = false,
        financeSuspectedFraud = true,
        financeCorrectionComment = "correction comm",
        impact = CorrectionImpactActionData.RepaymentByProject,
        impactComment = "repay comm",
        impactModificationStatusId = 6998L,
        measureEcPaymentId = 75L,
        measureYearId = 74L,
        measureYearStartDate = date7,
        measureYearEndDate = date8,
        measureScenario = ProjectCorrectionProgrammeMeasureScenarioData.SCENARIO_3,
        measureComment = "measure comm",
        measureIncludedInEcPaymentId = 62L,
        measureIncludedInEcPaymentYearId = 63L,
        measureIncludedInEcPaymentYearStartDate = date7,
        measureIncludedInEcPaymentYearEndDate = date8,
        measureIncludedInAccountYearId = 54L,
        measureIncludedInAccountYearStartDate = date5,
        measureIncludedInAccountYearEndDate = date6,
        includedTotal = BigDecimal.valueOf(654L, 2),
        includedTotalWithoutArt9495 = BigDecimal.valueOf(655L, 2),
        includedUnion = BigDecimal.valueOf(656L, 2),
        includedFundAmount = BigDecimal.valueOf(657L, 2),
        includedPrivateContribution = BigDecimal.valueOf(658L, 2),
        includedOfWhichPublic = BigDecimal.valueOf(659L, 2),
        includedOfWhichAutoPublic = BigDecimal.valueOf(660L, 2),
        includedOfWhichPrivate = BigDecimal.valueOf(661L, 2),
        includedComment = "included comment",
    )

    @MockK lateinit var auditControlPersistence: AuditControlPersistenceProvider
    @MockK lateinit var auditControlCorrectionPagingService: AuditControlCorrectionPagingService
    @MockK lateinit var correctionPersistence: AuditControlCorrectionPersistence
    @MockK lateinit var programmeMeasurePersistence: AuditControlCorrectionMeasurePersistence
    @MockK lateinit var financialDescriptionPersistence: AuditControlCorrectionFinancePersistence
    @MockK private lateinit var correctionRepository: AuditControlCorrectionRepository

    @InjectMockKs
    private lateinit var provider: AuditAndControlDataProviderImpl

    @Test
    fun fetchAllCorrectionsForExport() {
        every { correctionRepository.findAllCorrectionsForExport(4L) } returns Stream.of(correctionFetched)
        assertThat(provider.fetchAllCorrectionsForExport(4L)).asList().containsExactly(correctionExpected)
    }

}
