package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditurePersistenceProvider
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.AuditControlClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.AuditControlCorrectionClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.CombinationOfSelectedFundIsInvalidException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.CorrectionIdentificationValidator
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.ExpenditureNotValidException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.InvalidCorrectionScopeException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.LumpSumAndPartnerNotValidException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.PartnerReportNotValidException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.ProcurementNotValidException
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl.GetPartnerAndPartnerReportDataService
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFund
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class CorrectionIdentificationValidatorTest: UnitTest() {

    @MockK
    lateinit var allowedDataService: GetPartnerAndPartnerReportDataService

    @MockK
    lateinit var partnerReportExpenditurePersistenceProvider: ProjectPartnerReportExpenditurePersistenceProvider

    @MockK
    lateinit var partnerReportProcurementPersistence: ProjectPartnerReportProcurementPersistenceProvider

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var partnerReportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var correctionIdentificationValidator: CorrectionIdentificationValidator



    @Test
    fun `updateCorrection - audit control is closed exception`() {
        every { correctionPersistence.getByCorrectionId(16L) } returns mockk {
            every { auditControlId } returns 476L
            every { status } returns AuditControlStatus.Ongoing
        }
        every { auditControlPersistence.getById(476L) } returns mockk {
            every { status } returns AuditControlStatus.Closed
        }

        assertThrows<AuditControlClosedException> { correctionIdentificationValidator.validate(16L, mockk()) }
    }

    @Test
    fun `updateCorrection - correction is closed exception`() {
        every { correctionPersistence.getByCorrectionId(14L) } returns mockk {
            every { auditControlId } returns 475L
            every { status } returns AuditControlStatus.Closed
        }
        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
        }

        assertThrows<AuditControlCorrectionClosedException> { correctionIdentificationValidator.validate(14L, mockk()) }
    }


    @Test
    fun `linkedInvoice correction - report and fund combination exception`() {
        val reportId = 847L
        val fundId = 317L
        val correctionId = 16L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = reportId,
            lumpSumOrderNr = null,
            programmeFundId = 999L,
            costCategory = null,
            procurementId = null,
            expenditureId = null
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToCostOption
            every { status } returns AuditControlStatus.Ongoing
        }

        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }


        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf(
            CorrectionAvailablePartner(1L, 1, "", mockk(), false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(id = reportId, 1, null,
                        availableFunds = listOf(CorrectionAvailableFund(
                            ProgrammeFund(fundId, true, ProgrammeFundType.ERDF), mockk()
                        )),
                    ),
                ),
                availableFtls = emptyList(),
            )
        )
        assertThrows<CombinationOfSelectedFundIsInvalidException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }

    @Test
    fun `LinkedInvoice correction optional scope - throws invalid expenditure`() {

        val reportId = 847L
        val fundId = 317L
        val correctionId = 16L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = reportId,
            lumpSumOrderNr = null,
            programmeFundId = fundId,
            costCategory = null,
            procurementId = null,
            expenditureId = 77L
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToInvoice
            every { status } returns AuditControlStatus.Ongoing
            every { partnerId } returns 1L
        }

        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }


        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf(
            CorrectionAvailablePartner(1L, 1, "", mockk(), false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(id = reportId, 1, null,
                        availableFunds = listOf(CorrectionAvailableFund(
                            ProgrammeFund(fundId, true, ProgrammeFundType.ERDF), mockk()
                        )),
                    ),
                ),
                availableFtls = emptyList(),
            )
        )


        every { partnerReportExpenditurePersistenceProvider.existsByExpenditureId(
            partnerId = 1L,
            reportId = reportId,
            expenditureId = 77L
        ) } returns false

        assertThrows<ExpenditureNotValidException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }


    @Test
    fun `LinkedInvoice correction optional scope - throws invalid scope`() {

        val reportId = 847L
        val fundId = 317L
        val correctionId = 16L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = reportId,
            lumpSumOrderNr = null,
            programmeFundId = fundId,
            costCategory = null,
            procurementId = 29L,
            expenditureId = 77L
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToInvoice
            every { status } returns AuditControlStatus.Ongoing
            every { partnerId } returns 1L
        }

        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }


        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf(
            CorrectionAvailablePartner(1L, 1, "", mockk(), false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(id = reportId, 1, null,
                        availableFunds = listOf(CorrectionAvailableFund(
                            ProgrammeFund(fundId, true, ProgrammeFundType.ERDF), mockk()
                        )),
                    ),
                ),
                availableFtls = emptyList(),
            )
        )


        every { partnerReportExpenditurePersistenceProvider.existsByExpenditureId(
            partnerId = 1L,
            reportId = reportId,
            expenditureId = 77L
        ) } returns true

        assertThrows<InvalidCorrectionScopeException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }


    @Test
    fun `LinkedToCostOption correction optional scope - throws invalid scope`() {

        val reportId = 847L
        val fundId = 317L
        val correctionId = 16L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = reportId,
            lumpSumOrderNr = null,
            programmeFundId = fundId,
            costCategory = BudgetCostCategory.Office,
            procurementId = 29L,
            expenditureId = 77L
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToCostOption
            every { status } returns AuditControlStatus.Ongoing
            every { partnerId } returns 1L
        }

        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }


        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf(
            CorrectionAvailablePartner(1L, 1, "", mockk(), false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(id = reportId, 1, null,
                        availableFunds = listOf(CorrectionAvailableFund(
                            ProgrammeFund(fundId, true, ProgrammeFundType.ERDF), mockk()
                        )),
                    ),
                ),
                availableFtls = emptyList(),
            )
        )

        assertThrows<InvalidCorrectionScopeException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }

    @Test
    fun `LinkedToCostOption procurement not valid for selected report - throws exception`() {

        val reportId = 847L
        val fundId = 317L
        val correctionId = 16L
        val procurementId = 29L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = reportId,
            lumpSumOrderNr = null,
            programmeFundId = fundId,
            costCategory =  BudgetCostCategory.Office,
            procurementId = procurementId,
            expenditureId = null
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToCostOption
            every { status } returns AuditControlStatus.Ongoing
            every { partnerId } returns 1L
        }

        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }

        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf(
            CorrectionAvailablePartner(1L, 1, "", mockk(), false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(id = reportId, 1, null,
                        availableFunds = listOf(CorrectionAvailableFund(
                            ProgrammeFund(fundId, true, ProgrammeFundType.ERDF), mockk()
                        )),
                    )
                ),
                availableFtls = emptyList(),
            )
        )


     every { partnerReportPersistence.getReportIdsBefore(
         partnerId = 1L,
         beforeReportId = reportId
     ) } returns setOf(847)


       every { partnerReportProcurementPersistence.existsByProcurementIdAndPartnerReportIdIn(
           procurementId = procurementId,
           partnerReportIds = setOf(847)
       ) } returns false

        assertThrows<ProcurementNotValidException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }

    @Test
    fun `notLinkedInvoice correction - partner exception`() {
        val fundId = 317L
        val correctionId = 16L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = null,
            lumpSumOrderNr = 1,
            programmeFundId = fundId,
            costCategory = null,
            procurementId = null,
            expenditureId = null
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToCostOption
            every { status } returns AuditControlStatus.Ongoing
        }
        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }
        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf()

        assertThrows<LumpSumAndPartnerNotValidException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }
    @Test
    fun `linkedInvoice correction - partner report exception`() {
        val fundId = 317L
        val correctionId = 16L

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerId = 1L,
            partnerReportId = 1L,
            lumpSumOrderNr = null,
            programmeFundId = fundId,
            costCategory = null,
            procurementId = null,
            expenditureId = null
        )

        every { correctionPersistence.getByCorrectionId(correctionId) } returns mockk {
            every { auditControlId } returns 475L
            every { type } returns AuditControlCorrectionType.LinkedToCostOption
            every { status } returns AuditControlStatus.Ongoing
        }
        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }
        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf()

        assertThrows<PartnerReportNotValidException> { correctionIdentificationValidator.validate(correctionId, toUpdate) }
    }


}
