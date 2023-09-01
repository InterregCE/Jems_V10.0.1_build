package io.cloudflight.jems.server.project.controller.report.project.verification.expenditure

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportInvestmentDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ExpenditureParkingMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectPartnerReportExpenditureItemDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdateDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationRiskBasedDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditure.GetProjectReportVerificationExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditureRiskBased.GetProjectReportVerificationExpenditureRiskBasedInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditure.UpdateProjectReportVerificationExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased.UpdateProjectReportVerificationExpenditureRiskBasedInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportVerificationExpenditureControllerTest: UnitTest() {

    companion object{
        private const val PROJECT_ID = 1L
        private const val PROJECT_REPORT_ID = 20L
        private const val REPORT_ID = 101L
        private const val PARTNER_ID = 10L
        private const val TYPOLOGY_OF_ERROR_ID = 3L
        private const val EXPENDITURE_ID = 1L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
        private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

        private val parkingMetadataDTO = ExpenditureParkingMetadataDTO(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = 20,
            originalExpenditureNumber = 3
        )

        private val parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = PROJECT_REPORT_ID,
            originalExpenditureNumber = 3
        )

        private val dummyLineLumpSum = ProjectPartnerReportLumpSum(
            id = 36L,
            lumpSumProgrammeId = 945L,
            fastTrack = false,
            orderNr = 17,
            period = 4,
            cost = BigDecimal.ONE,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
        )

        private val expectedDummyLineLumpSum = ProjectPartnerReportLumpSumDTO(
            id = 36L,
            lumpSumProgrammeId = 945L,
            period = 4,
            cost = BigDecimal.ONE,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
        )

        private val expectedDummyInvestmentLine = ProjectPartnerReportInvestmentDTO(
            id = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            deactivated = false,
        )

        private val dummyInvestmentLine = ProjectPartnerReportInvestment(
            id = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            total = BigDecimal.ONE,
            deactivated = false,
        )

        private val expectedDummyLineUnitCost = ProjectPartnerReportUnitCostDTO(
            id = 44L,
            unitCostProgrammeId = 945L,
            projectDefined = false,
            costPerUnit = BigDecimal.ONE,
            numberOfUnits = BigDecimal.TEN,
            total = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            category = BudgetCategoryDTO.ExternalCosts,
        )

        private val dummyLineUnitCost = ProjectPartnerReportUnitCost(
            id = 44L,
            unitCostProgrammeId = 945L,
            projectDefined = false,
            costPerUnit = BigDecimal.ONE,
            numberOfUnits = BigDecimal.TEN,
            total = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            category = ReportBudgetCategory.ExternalCosts,
        )

        private val expectedProcurement = ProjectPartnerReportProcurementDTO(
            id = 265,
            reportId = REPORT_ID,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = NEXT_WEEK,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
            comment = "comment 265",
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 265,
            reportId = REPORT_ID,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = NEXT_WEEK,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
            comment = "comment 265",
        )

        private val expenditureItem = ProjectPartnerReportExpenditureItem(
            id = EXPENDITURE_ID,
            number = 1,

            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,

            partnerReportId = REPORT_ID,
            partnerReportNumber = 1,

            lumpSum = dummyLineLumpSum,
            unitCost = dummyLineUnitCost,
            gdpr = false,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            investment = dummyInvestmentLine,
            contract = procurement,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(22),
            vat = BigDecimal.valueOf(18.0),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(31.2),
            currencyCode = "CZK",
            currencyConversionRate = BigDecimal.valueOf(24),
            declaredAmountAfterSubmission = BigDecimal.valueOf(1.3),
            attachment = JemsFileMetadata(500L, "file.txt", UPLOADED),

            partOfSample = false,
            partOfSampleLocked = false,
            certifiedAmount = BigDecimal.valueOf(101),
            deductedAmount = BigDecimal.valueOf(101),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = true,
            verificationComment = "VERIFICATION COMM",

            parkingMetadata = parkingMetadata

        )

        private val expenditures =
            ProjectReportVerificationExpenditureLine(
                expenditure = expenditureItem,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                amountAfterVerification = BigDecimal.valueOf(300),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = true,
                verificationComment = "VERIFICATION COMM"
            )

        private val expectedExpenditureItem = ProjectPartnerReportExpenditureItemDTO(
            id = EXPENDITURE_ID,
            number = 1,

            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = 1,

            partnerReportId = REPORT_ID,
            partnerReportNumber = 1,

            lumpSum = expectedDummyLineLumpSum,
            unitCost = expectedDummyLineUnitCost,
            gdpr = false,
            costCategory = BudgetCategoryDTO.EquipmentCosts,
            investment = expectedDummyInvestmentLine,
            contract = expectedProcurement,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(22),
            vat = BigDecimal.valueOf(18.0),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(31.2),
            currencyCode = "CZK",
            currencyConversionRate = BigDecimal.valueOf(24),
            declaredAmountAfterSubmission = BigDecimal.valueOf(1.3),
            attachment = JemsFileMetadataDTO(500L, "file.txt", UPLOADED),

            partOfSample = false,
            partOfSampleLocked = false,
            certifiedAmount = BigDecimal.valueOf(101),
            deductedAmount = BigDecimal.valueOf(101),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = true,
            verificationComment = "VERIFICATION COMM",

            parkingMetadata = parkingMetadataDTO

        )

        private val expectedExpenditures =
            ProjectReportVerificationExpenditureLineDTO(
                expenditure = expectedExpenditureItem,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                amountAfterVerification = BigDecimal.valueOf(300),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = true,
                parkingMetadata = null,
                verificationComment = "VERIFICATION COMM"
            )


        val expendituresToUpdate = listOf(
            ProjectReportVerificationExpenditureLineUpdate(
                expenditureId = EXPENDITURE_ID,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = false,
                verificationComment = "VERIFICATION COMM"
            )
        )

        val expendituresToUpdateDTO = listOf(
            ProjectReportVerificationExpenditureLineUpdateDTO(
                expenditureId = EXPENDITURE_ID,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = false,
                verificationComment = "VERIFICATION COMM"
            )
        )

        val expectedExpendituresAfterUpdate = listOf(
            ProjectReportVerificationExpenditureLineDTO(
                expenditure = expectedExpenditureItem,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                amountAfterVerification = BigDecimal.valueOf(300),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = true,
                parkingMetadata = null,
                verificationComment = "VERIFICATION COMM"
            )
        )

        val riskBasedData = ProjectReportVerificationRiskBased(
            projectReportId = PROJECT_REPORT_ID,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "VERIFICATION COMM"
        )

        val expectedRiskBasedData = ProjectReportVerificationRiskBasedDTO(
            projectReportId = PROJECT_REPORT_ID,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "VERIFICATION COMM"
        )


    }

    @MockK
    lateinit var getExpenditureVerificationInteractor: GetProjectReportVerificationExpenditureInteractor

    @MockK
    lateinit var getExpenditureVerificationRiskBasedInteractor: GetProjectReportVerificationExpenditureRiskBasedInteractor

    @MockK
    lateinit var updateExpenditureVerificationInteractor: UpdateProjectReportVerificationExpenditureInteractor

    @MockK
    lateinit var updateExpenditureVerificationRiskBasedInteractor: UpdateProjectReportVerificationExpenditureRiskBasedInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportVerificationExpenditureController

    @Test
    fun getProjectReportExpenditureVerificationTest() {
        every {
            getExpenditureVerificationInteractor.getExpenditureVerification(
                PROJECT_REPORT_ID
            )
        } returns listOf(expenditures)

        Assertions.assertThat(
            controller.getProjectReportExpenditureVerification(
                PROJECT_ID,
                PROJECT_REPORT_ID
            )
        ).containsExactly(expectedExpenditures)
    }

    @Test
    fun updateProjectReportExpendituresVerificationTest() {
        every {
            updateExpenditureVerificationInteractor.updateExpenditureVerification(
                PROJECT_REPORT_ID,
                expendituresToUpdate
            )
        } returns listOf(expenditures)

        Assertions.assertThat(
            controller.updateProjectReportExpendituresVerification(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                expendituresToUpdateDTO
            )
        ).isEqualTo(expectedExpendituresAfterUpdate)
    }

    @Test
    fun getProjectReportExpenditureVerificationRiskBasedTest() {
        every {
            getExpenditureVerificationRiskBasedInteractor.getExpenditureVerificationRiskBasedData(
                PROJECT_ID,
                PROJECT_REPORT_ID
            )
        } returns riskBasedData

        Assertions.assertThat(
            controller.getProjectReportExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID
            )
        ).isEqualTo(expectedRiskBasedData)
    }

    @Test
    fun updateProjectReportExpenditureVerificationRiskBasedTest() {
        every {
            updateExpenditureVerificationRiskBasedInteractor.updateExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID,
                riskBasedData
            )
        } returns riskBasedData

        Assertions.assertThat(
            controller.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_ID,
                PROJECT_REPORT_ID,
                riskBasedData.toDto()
            )
        ).isEqualTo(
            expectedRiskBasedData
        )
    }
}
