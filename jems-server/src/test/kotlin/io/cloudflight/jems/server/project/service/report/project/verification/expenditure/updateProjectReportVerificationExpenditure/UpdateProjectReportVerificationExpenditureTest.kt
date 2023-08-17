package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class UpdateProjectReportVerificationExpenditureTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PROJECT_REPORT_ID = 20L
        private const val TYPOLOGY_OF_ERROR_ID = 3L
        private const val EXPENDITURE_ID = 1L
        private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

        private const val REPORT_ID = 101L
        private const val PARTNER_ID = 10L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)

        private val parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 3
        )


        private val dummyLineLumpSum = ProjectPartnerReportLumpSum(
            id = 36L,
            lumpSumProgrammeId = 945L,
            fastTrack = false,
            orderNr = 7,
            period = 4,
            cost = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
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

        private val expenditureLine = ProjectReportVerificationExpenditureLine(
            expenditure = expenditureItem,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(300),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = false,
            verificationComment = "VERIFICATION COMM"
        )

        private val expectedExpenditureLine = ProjectReportVerificationExpenditureLine(
            expenditure = expenditureItem,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(300),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = false,
            verificationComment = "NEW VERIFICATION COMM"
        )

        fun expendituresToUpdate(verificationComment: String) = listOf(
            ProjectReportVerificationExpenditureLineUpdate(
                expenditureId = EXPENDITURE_ID,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(150),
                deductedByMa = BigDecimal.valueOf(250),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = false,
                verificationComment = verificationComment
            )
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @MockK
    lateinit var partnerReportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence

    @MockK
    lateinit var typologyPersistence: ProgrammeTypologyErrorsPersistence

    @InjectMockKs
    lateinit var generalValidator: GeneralValidatorDefaultImpl

    @InjectMockKs
    lateinit var updateProjectReportVerificationExpenditure: UpdateProjectReportVerificationExpenditure

    @ParameterizedTest(name = "updateExpenditureVerification - {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"])
    fun updateExpenditureVerification(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { reportPersistence.getReportByIdUnSecured(PROJECT_REPORT_ID) } returns report

        every {
            projectReportExpenditureVerificationPersistence.getProjectReportExpenditureVerification(
                PROJECT_REPORT_ID
            )
        } returns listOf(expenditureLine)
        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerification(
                PROJECT_REPORT_ID,
                any()
            )
        } returns listOf(expectedExpenditureLine)
        every { partnerReportParkedExpenditurePersistence.parkExpenditures(any()) } returns Unit
        every { partnerReportParkedExpenditurePersistence.unParkExpenditures(any()) } returns Unit

        every { typologyPersistence.getAllTypologyErrors() } returns listOf(TypologyErrors(PROJECT_ID, "Typology 1"))

        assertThat(
            updateProjectReportVerificationExpenditure.updateExpenditureVerification(
                PROJECT_REPORT_ID,
                expendituresToUpdate("NEW COMMENT VERIFICATION")
            )
        ).isEqualTo(
            listOf(expectedExpenditureLine)
        )
    }

    @ParameterizedTest(name = "updateExpenditureVerification - wrong status {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.EXCLUDE)
    fun `updateExpenditureVerification - wrong status`(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { reportPersistence.getReportByIdUnSecured(PROJECT_REPORT_ID) } returns report

        assertThrows<VerificationNotOpen> {
            updateProjectReportVerificationExpenditure.updateExpenditureVerification(
                PROJECT_REPORT_ID,
                expendituresToUpdate("NEW COMMENT VERIFICATION"),
            )
        }
    }

    @Test
    fun `should throw AppInputValidationException when verification comment length is not valid`() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.InVerification
        every { reportPersistence.getReportByIdUnSecured(PROJECT_REPORT_ID) } returns report

        every {
            projectReportExpenditureVerificationPersistence.getProjectReportExpenditureVerification(
                PROJECT_REPORT_ID
            )
        } returns listOf(expenditureLine)
        every {
            projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerification(
                PROJECT_REPORT_ID,
                any()
            )
        } returns listOf(expectedExpenditureLine)
        every { partnerReportParkedExpenditurePersistence.parkExpenditures(any()) } returns Unit
        every { partnerReportParkedExpenditurePersistence.unParkExpenditures(any()) } returns Unit

        every { typologyPersistence.getAllTypologyErrors() } returns listOf(TypologyErrors(PROJECT_ID, "Typology 1"))

        assertThrows<AppInputValidationException> {
            updateProjectReportVerificationExpenditure.updateExpenditureVerification(PROJECT_REPORT_ID, expendituresToUpdate("a".repeat(1001)))
        }

    }
}
