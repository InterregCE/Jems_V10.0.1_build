package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionCostItemDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionFollowUpTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AuditControlCorrectionImpactDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.CorrectionImpactActionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection.CreateAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.deleteAuditControlCorrection.DeleteAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.getAuditControlCorrection.GetAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection.ListAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.UpdateAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection.CloseAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionAvailableProcurements.GetCorrectionAvailableProcurementsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionCostItems.GetCorrectionCostItemsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.listPreviouslyClosedCorrection.ListPreviouslyClosedCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class AuditControlCorrectionControllerTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val CORRECTION_ID = 1L

        private val correction = AuditControlCorrectionDetail(
            id = 1L,
            orderNr = 10,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNr = 36,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
            impact = AuditControlCorrectionImpact(
                action = CorrectionImpactAction.RepaymentByNA,
                comment = "impact comment",
            ),
            costCategory = null,
            procurementId = null,
            expenditureCostItem = null,
        )

        private val expectedCorrection = ProjectAuditControlCorrectionDTO(
            id = 1L,
            orderNr = 10,
            status = AuditStatusDTO.Ongoing,
            type = AuditControlCorrectionTypeDTO.LinkedToInvoice,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNumber = 36,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpTypeDTO.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
            impact = AuditControlCorrectionImpactDTO(
                action = CorrectionImpactActionDTO.RepaymentByNA,
                comment = "impact comment",
            ),
            costCategory = null,
            procurementId = null,
            expenditureCostItem = null,
        )

        private val extendedCorrection = AuditControlCorrectionDetail(
            id = 1L,
            orderNr = 10,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToCostOption,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNr = 15,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
            impact = AuditControlCorrectionImpact(
                action = CorrectionImpactAction.RepaymentByNA,
                comment = "impact comment",
            ),
            costCategory = null,
            procurementId = null,
            expenditureCostItem = null,
        )
        private val expectedExtendedCorrection = ProjectAuditControlCorrectionDTO(
            id = 1L,
            orderNr = 10,
            status = AuditStatusDTO.Ongoing,
            type = AuditControlCorrectionTypeDTO.LinkedToCostOption,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNumber = 15,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpTypeDTO.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
            impact = AuditControlCorrectionImpactDTO(
                action = CorrectionImpactActionDTO.RepaymentByNA,
                comment = "impact comment",
            ),
            costCategory = null,
            procurementId = null,
            expenditureCostItem = null,
        )

        private val correctionLines = listOf(
            AuditControlCorrectionLine(
                id = CORRECTION_ID,
                orderNr = 1,
                status = AuditControlStatus.Ongoing,
                type = AuditControlCorrectionType.LinkedToCostOption,
                auditControlId = AUDIT_CONTROL_ID,
                auditControlNr = 1,
                canBeDeleted = true,
                partnerReport = 2,
                partnerId = 777L,
                partnerRole = ProjectPartnerRole.PARTNER,
                partnerNumber = 3,
                partnerDisabled = false,
                followUpAuditNr = null,
                followUpCorrectionNr = null,
                fund = ProgrammeFund(
                    id = 622L,
                    selected = true,
                    type = ProgrammeFundType.NEIGHBOURHOOD_CBC,
                    abbreviation = setOf(InputTranslation(SystemLanguage.GA, "abbr-GA")),
                    description = setOf(InputTranslation(SystemLanguage.FI, "desc-FI")),
                ),
                fundAmount = BigDecimal.valueOf(1000),
                publicContribution = BigDecimal.valueOf(3000),
                autoPublicContribution = BigDecimal.valueOf(4000),
                privateContribution = BigDecimal.valueOf(5000),
                total = BigDecimal.valueOf(10000),
                impactProjectLevel = CorrectionImpactAction.RepaymentByProject,
                scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            )
        )
        private val expectedCorrectionLines = listOf(
            ProjectAuditControlCorrectionLineDTO(
                id = CORRECTION_ID,
                auditControlId = AUDIT_CONTROL_ID,
                orderNr = 1,
                status = AuditStatusDTO.Ongoing,
                type = AuditControlCorrectionTypeDTO.LinkedToCostOption,
                auditControlNr = 1,
                canBeDeleted = true,


                partnerRole = ProjectPartnerRoleDTO.PARTNER,
                partnerNumber = 3,
                partnerDisabled = false,
                partnerReport = 2,
                followUpAuditNr = null,
                followUpCorrectionNr = null,
                fund = ProgrammeFundDTO(
                    id = 622L,
                    selected = true,
                    type = ProgrammeFundTypeDTO.NEIGHBOURHOOD_CBC,
                    abbreviation = setOf(InputTranslation(SystemLanguage.GA, "abbr-GA")),
                    description = setOf(InputTranslation(SystemLanguage.FI, "desc-FI")),
                ),
                fundAmount = BigDecimal.valueOf(1000),
                publicContribution = BigDecimal.valueOf(3000),
                autoPublicContribution = BigDecimal.valueOf(4000),
                privateContribution = BigDecimal.valueOf(5000),
                total = BigDecimal.valueOf(10000),
                impactProjectLevel = CorrectionImpactActionDTO.RepaymentByProject,
                scenario = ProjectCorrectionProgrammeMeasureScenarioDTO.SCENARIO_5,
            )
        )

        val correctionCostItems = listOf(
            CorrectionCostItem(
                id = 21L,
                number = 2,
                partnerReportNumber = 3,
                lumpSum = null,
                unitCost = null,
                costCategory = ReportBudgetCategory.StaffCosts,
                investmentId = null,
                investmentNumber = null,
                investmentWorkPackageNumber = null,
                contractId = 821L,
                internalReferenceNumber = null,
                invoiceNumber = null,
                invoiceDate = null,
                description = emptySet(),
                comment = emptySet(),
                declaredAmount = BigDecimal.valueOf(1233.33),
                currencyCode = "EUR",
                declaredAmountAfterSubmission = BigDecimal.valueOf(1233.33)
            )
        )

        val correctionCostItemsDTOs = listOf(
            CorrectionCostItemDTO(
                id = 21L,
                number = 2,
                partnerReportNumber = 3,
                lumpSum = null,
                unitCost = null,
                costCategory = BudgetCategoryDTO.StaffCosts,
                investmentId = null,
                investmentNumber = null,
                investmentWorkPackageNumber = null,
                contractId = 821L,
                internalReferenceNumber = null,
                invoiceNumber = null,
                invoiceDate = null,
                description = emptySet(),
                comment = emptySet(),
                declaredAmount = BigDecimal.valueOf(1233.33),
                currencyCode = "EUR",
                declaredAmountAfterSubmission = BigDecimal.valueOf(1233.33)
            )
        )

    }

    @MockK
    lateinit var createProjectCorrection: CreateAuditControlCorrectionInteractor

    @MockK
    lateinit var listProjectAuditCorrections: ListAuditControlCorrectionInteractor

    @MockK
    lateinit var getProjectAuditCorrection: GetAuditControlCorrectionInteractor

    @MockK
    lateinit var deleteProjectAuditCorrection: DeleteAuditControlCorrectionInteractor

    @MockK
    lateinit var closeProjectCorrection: CloseAuditControlCorrectionInteractor

    @MockK
    private lateinit var updateCorrection: UpdateAuditControlCorrectionInteractor

    @MockK
    private lateinit var listPreviouslyClosedCorrection: ListPreviouslyClosedCorrectionInteractor

    @MockK
    private lateinit var getAvailableProcurements: GetCorrectionAvailableProcurementsInteractor

    @MockK
    private lateinit var getCorrectionCostItems: GetCorrectionCostItemsInteractor

    @InjectMockKs
    lateinit var projectAuditCorrectionController: AuditControlCorrectionController

    @Test
    fun createProjectAuditCorrection() {
        every {
            createProjectCorrection.createCorrection(AUDIT_CONTROL_ID, AuditControlCorrectionType.LinkedToCostOption)
        } returns correction

        assertThat(
            projectAuditCorrectionController.createProjectAuditCorrection(0L, AUDIT_CONTROL_ID, AuditControlCorrectionTypeDTO.LinkedToCostOption)
        ).isEqualTo(expectedCorrection)
    }

    @Test
    fun listProjectAuditCorrections() {
        every {
            listProjectAuditCorrections.listCorrections(AUDIT_CONTROL_ID, Pageable.unpaged())
        } returns PageImpl(correctionLines)

        assertThat(
            projectAuditCorrectionController.listProjectAuditCorrections(0L, AUDIT_CONTROL_ID, Pageable.unpaged())
        ).containsExactlyElementsOf(expectedCorrectionLines)
    }

    @Test
    fun getProjectAuditCorrection() {
        every {
            getProjectAuditCorrection.getCorrection(CORRECTION_ID)
        } returns extendedCorrection

        assertThat(
            projectAuditCorrectionController.getProjectAuditCorrection(0L, AUDIT_CONTROL_ID, CORRECTION_ID)
        ).isEqualTo(expectedExtendedCorrection)
    }

    @Test
    fun closeProjectCorrection() {
        every {
            closeProjectCorrection.closeCorrection(45L)
        } returns AuditControlStatus.Closed

        assertThat(
            projectAuditCorrectionController.closeProjectCorrection(0L, 0L, 45L)
        ).isEqualTo(AuditStatusDTO.Closed)

        verify(exactly = 1) { closeProjectCorrection.closeCorrection(45L) }
    }

    @Test
    fun getAvailableProcurements() {
        every { getAvailableProcurements.getAvailableProcurements(CORRECTION_ID) } returns listOf(IdNamePair(1L, "PC01"))
        assertThat(
            projectAuditCorrectionController.listCorrectionAvailableProcurements(0L, AUDIT_CONTROL_ID, CORRECTION_ID)
        ).containsExactly(IdNamePairDTO(1L, "PC01"))
    }

    @Test
    fun getAvailableCostItems() {
        every { getCorrectionCostItems.getCostItems(CORRECTION_ID, Pageable.unpaged()) } returns PageImpl(correctionCostItems)
        assertThat(
            projectAuditCorrectionController.listCorrectionAvailableCostItems(0L, AUDIT_CONTROL_ID, CORRECTION_ID, Pageable.unpaged())
        ).containsExactlyElementsOf(correctionCostItemsDTOs)
    }

}
