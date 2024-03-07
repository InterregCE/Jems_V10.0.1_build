package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumTranslId
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslId
import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.control.expenditure.PartnerReportParkedExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedLinked
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectPartnerReportExpenditurePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 380L
        private const val PROCUREMENT_ID = 18L

        private const val EXPENDITURE_TO_UPDATE = 40L
        private const val EXPENDITURE_TO_DELETE = 41L
        private const val EXPENDITURE_TO_STAY = 42L
        private const val EXPENDITURE_TO_ADD_1 = -1L
        private const val EXPENDITURE_TO_ADD_2 = -2L

        private val TODAY = ZonedDateTime.now()
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        val dummyAttachment = JemsFileMetadataEntity(
            id = 970L,
            projectId = 4L,
            partnerId = PARTNER_ID,
            path = "",
            minioBucket = "minioBucket",
            minioLocation = "",
            name = "some_file.txt",
            type = mockk(),
            size = 1475,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
            description = "dummy description of attachment",
        )

        private fun dummyExpenditure(
            id: Long,
            report: ProjectPartnerReportEntity,
            lumpSum: PartnerReportLumpSumEntity? = null,
            unitCost: PartnerReportUnitCostEntity? = null,
            investment: PartnerReportInvestmentEntity? = null,
            unParkedFrom: PartnerReportExpenditureCostEntity? = null,
        ) = PartnerReportExpenditureCostEntity(
            id = id,
            number = 1,
            partnerReport = report,
            reportLumpSum = lumpSum,
            reportUnitCost = unitCost,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            gdpr = false,
            reportInvestment = investment,
            procurementId = PROCUREMENT_ID,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            translatedValues = mutableSetOf(),
            attachment = dummyAttachment,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(3680),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null,
            parked = false,
            reIncludedFromExpenditure = unParkedFrom,
            reportOfOrigin = if (unParkedFrom == null) null else report,
            originalNumber = if (unParkedFrom == null) null else 14,
            parkedInProjectReport = null,
            partOfSampleLocked = false
        ).apply {
            translatedValues.add(
                PartnerReportExpenditureCostTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    comment = "comment EN",
                    description = "desc EN",
                )
            )
        }

        private fun dummyExpectedExpenditure(
            id: Long,
            lumpSumId: Long?,
            unitCostId: Long?,
            investmentId: Long?,
            number: Int,
        ) = ProjectPartnerReportExpenditureCost(
            id = id,
            number = number,
            lumpSumId = lumpSumId,
            unitCostId = unitCostId,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            gdpr = false,
            investmentId = investmentId,
            contractId = PROCUREMENT_ID + 10,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            attachment = JemsFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
            parkingMetadata = ExpenditureParkingMetadata(
                reportOfOriginId = 75L,
                reportProjectOfOriginId = null,
                reportOfOriginNumber = 4,
                originalExpenditureNumber = 8,
                parkedFromExpenditureId = id,
                parkedOn = TODAY
            ),
        )

        private fun dummyExpectedParkedExpenditure() = ProjectPartnerReportParkedExpenditure(
            expenditure = dummyExpectedExpenditure(id = 14L, 828L, 829L, 830L, 1)
                .copy(
                    contractId = PROCUREMENT_ID,
                    parkingMetadata = ExpenditureParkingMetadata(
                        reportOfOriginId = 80L,
                        reportOfOriginNumber = 81,
                        reportProjectOfOriginId = null,
                        originalExpenditureNumber = 14,
                        parkedFromExpenditureId = 14L,
                        parkedOn = null
                    )
                ),
            lumpSum = ProjectPartnerReportParkedLinked(828L, 8281L, 4, false),
            lumpSumName = setOf(InputTranslation(SystemLanguage.EN, "name ls")),
            unitCost = ProjectPartnerReportParkedLinked(829L, 8291L, null, false),
            unitCostName = setOf(InputTranslation(SystemLanguage.EN, "name uc")),
            investment = ProjectPartnerReportParkedLinked(830L, 8301L, null, false),
            investmentName = "I14.11",
        )

        private fun dummyExpectedExpenditureNew(
            id: Long,
            lumpSumId: Long?,
            unitCostId: Long?,
            investmentId: Long?,
            number: Int,
        ) = ProjectPartnerReportExpenditureCost(
            id = id,
            number = number,
            lumpSumId = lumpSumId,
            unitCostId = unitCostId,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            gdpr = true,
            investmentId = investmentId,
            contractId = PROCUREMENT_ID + 10,
            internalReferenceNumber = "irn NEW",
            invoiceNumber = "invoice NEW",
            invoiceDate = YESTERDAY.minusDays(1),
            dateOfPayment = TOMORROW.plusDays(1),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN NEW")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
            totalValueInvoice = BigDecimal.ZERO,
            vat = BigDecimal.TEN,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.ONE,
            currencyCode = "HUF",
            currencyConversionRate = BigDecimal.valueOf(368),
            declaredAmountAfterSubmission = BigDecimal.valueOf(3680),
            attachment = null,
            parkingMetadata = ExpenditureParkingMetadata(
                reportOfOriginId = 75L,
                reportOfOriginNumber = 4,
                reportProjectOfOriginId = null,
                originalExpenditureNumber = 8,
                parkedFromExpenditureId = id,
                parkedOn = TODAY
            ),
        )

        private fun dummyLumpSumEntity(reportEntity: ProjectPartnerReportEntity) = PartnerReportLumpSumEntity(
            id = 4L,
            reportEntity = reportEntity,
            programmeLumpSum = ProgrammeLumpSumEntity(
                id = 400L,
                translatedValues = mutableSetOf(
                    ProgrammeLumpSumTranslEntity(
                        ProgrammeLumpSumTranslId(
                            400L,
                            SystemLanguage.EN
                        ), "name EN", "desc EN"
                    )
                ),
                cost = BigDecimal.TEN,
                splittingAllowed = true,
                phase = ProgrammeLumpSumPhase.Implementation,
                categories = mutableSetOf(),
                isFastTrack = false
            ),
            orderNr = 10,
            period = 2,
            total = BigDecimal.ONE,
            current = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.TEN,
            previouslyReported = BigDecimal.ZERO,
            previouslyPaid = BigDecimal.valueOf(1111, 1),
            currentParked = BigDecimal.valueOf(200),
            currentReIncluded = BigDecimal.valueOf(1000),
            previouslyReportedParked = BigDecimal.valueOf(1000),
            previouslyValidated = BigDecimal.valueOf(5)
        )

        private fun dummyUnitCostEntity(reportEntity: ProjectPartnerReportEntity) = PartnerReportUnitCostEntity(
            id = 4L,
            reportEntity = reportEntity,
            programmeUnitCost = ProgrammeUnitCostEntity(
                id = 400L,
                projectId = null,
                translatedValues = mutableSetOf(
                    ProgrammeUnitCostTranslEntity(
                        ProgrammeUnitCostTranslId(
                            400L,
                            SystemLanguage.EN
                        ), "name EN", "desc EN"
                    )
                ),
                isOneCostCategory = false,
                costPerUnit = BigDecimal.ONE,
                costPerUnitForeignCurrency = BigDecimal.TEN,
                foreignCurrencyCode = "RON",
                categories = mutableSetOf(
                    ProgrammeUnitCostBudgetCategoryEntity(
                        id = 1L,
                        programmeUnitCostId = 5L,
                        category = BudgetCategory.StaffCosts
                    ),
                    ProgrammeUnitCostBudgetCategoryEntity(
                        id = 2L,
                        programmeUnitCostId = 5L,
                        category = BudgetCategory.EquipmentCosts
                    )
                ),
            ),
            numberOfUnits = BigDecimal.ONE,
            total = BigDecimal.ONE,
            current = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.TEN,
            previouslyReported = BigDecimal.ZERO,
            currentParked = BigDecimal.TEN,
            currentReIncluded = BigDecimal.valueOf(50),
            previouslyReportedParked = BigDecimal.ZERO,
            previouslyValidated = BigDecimal.valueOf(6)
        )

        private fun dummyInvestmentEntity(reportEntity: ProjectPartnerReportEntity) = PartnerReportInvestmentEntity(
            id = 7L,
            reportEntity = reportEntity,
            investmentId = 18L,
            investmentNumber = 2,
            workPackageNumber = 1,
            translatedValues = mutableSetOf(),
            total = BigDecimal.ONE,
            current = BigDecimal.ONE,
            totalEligibleAfterControl = BigDecimal.TEN,
            previouslyReported = BigDecimal.ZERO,
            currentParked = BigDecimal.valueOf(200),
            currentReIncluded = BigDecimal.valueOf(1000),
            previouslyReportedParked = BigDecimal.valueOf(1000),
            deactivated = false,
            previouslyValidated = BigDecimal.valueOf(7)
        ).apply {
            translatedValues.add(
                PartnerReportInvestmentTranslEntity(TranslationId(this, SystemLanguage.EN), "inv title EN")
            )
        }

        private val dummyLumpSum = ProjectPartnerReportLumpSum(
            id = 4L,
            lumpSumProgrammeId = 400L,
            fastTrack = false,
            orderNr = 10,
            period = 2,
            cost = BigDecimal.ONE,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN"))
        )

        private val dummyUnitCost = ProjectPartnerReportUnitCost(
            id = 4L,
            unitCostProgrammeId = 400L,
            projectDefined = false,
            total = BigDecimal.ONE,
            numberOfUnits = BigDecimal.ONE,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN")),
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "RON",
            category = ReportBudgetCategory.Multiple
        )

        private val dummyInvestment = ProjectPartnerReportInvestment(
            id = 7L,
            investmentId = 18L,
            workPackageNumber = 1,
            investmentNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "inv title EN")),
            total = BigDecimal.ONE,
            deactivated = false,
        )

        private val dummyBudgetOptions = ProjectPartnerBudgetOptions(
            officeAndAdministrationOnStaffCostsFlatRate = 20,
            officeAndAdministrationOnDirectCostsFlatRate = 22,
            travelAndAccommodationOnStaffCostsFlatRate = 8,
            staffCostsFlatRate = 1,
            partnerId = PARTNER_ID
        )

        private fun parkedFrom(
            report: ProjectPartnerReportEntity,
            lumpSum: PartnerReportLumpSumEntity?,
            unitCost: PartnerReportUnitCostEntity?,
            investment: PartnerReportInvestmentEntity?,
            unParkedFrom: PartnerReportExpenditureCostEntity?,
            reportOfOrigin: ProjectPartnerReportEntity?,
        ) = PartnerReportExpenditureCostEntity(
            id = 4985L,
            number = 19,
            partnerReport = report,
            reportLumpSum = lumpSum,
            reportUnitCost = unitCost,
            costCategory = ReportBudgetCategory.StaffCosts,
            reportInvestment = investment,
            procurementId = 177L,
            internalReferenceNumber = "internalReferenceNumber",
            invoiceNumber = "invoiceNumber",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.TEN,
            pricePerUnit = BigDecimal.ONE,
            declaredAmount = BigDecimal.ZERO,
            currencyCode = "currencyCode",
            currencyConversionRate = BigDecimal.TEN,
            declaredAmountAfterSubmission = BigDecimal.ONE,
            partOfSample = false,
            certifiedAmount = BigDecimal.ZERO,
            deductedAmount = BigDecimal.TEN,
            typologyOfErrorId = 48L,
            parked = false,
            verificationComment = "verif-com",
            translatedValues = mutableSetOf(
                PartnerReportExpenditureCostTranslEntity(TranslationId(mockk(), SystemLanguage.EN), "comm", "desc"),
            ),
            attachment = null,
            reIncludedFromExpenditure = unParkedFrom,
            reportOfOrigin = reportOfOrigin,
            originalNumber = if (reportOfOrigin == null) null else 42,
            parkedInProjectReport = null,
            partOfSampleLocked = false,
        )

        private fun parkedFromExpected() = ProjectPartnerReportExpenditureCost(
            id = 0L,
            number = 0,
            lumpSumId = 636L,
            unitCostId = 637L,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
            investmentId = 638L,
            contractId = 177L,
            internalReferenceNumber = "internalReferenceNumber",
            invoiceNumber = "invoiceNumber",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comm")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.TEN,
            pricePerUnit = BigDecimal.ONE,
            declaredAmount = BigDecimal.ZERO,
            currencyCode = "currencyCode",
            currencyConversionRate = BigDecimal.TEN,
            declaredAmountAfterSubmission = BigDecimal.ONE,
            attachment = null,
            parkingMetadata = ExpenditureParkingMetadata(
                reportOfOriginId = 11L,
                reportOfOriginNumber = 111,
                reportProjectOfOriginId = null,
                originalExpenditureNumber = 4,
                parkedFromExpenditureId = 0,
                parkedOn = null
            ),
        )
    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportExpenditureRepository: ProjectPartnerReportExpenditureRepository

    @MockK
    lateinit var reportExpenditureParkedRepository: PartnerReportParkedExpenditureRepository

    @MockK
    lateinit var reportLumpSumRepository: ProjectPartnerReportLumpSumRepository

    @MockK
    lateinit var reportUnitCostRepository: ProjectPartnerReportUnitCostRepository

    @MockK
    lateinit var reportInvestmentRepository: ProjectPartnerReportInvestmentRepository

    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @MockK
    lateinit var reportCostCategoriesRepository: ReportProjectPartnerExpenditureCostCategoryRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportExpenditurePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            reportExpenditureRepository,
            reportLumpSumRepository,
            reportUnitCostRepository,
            reportInvestmentRepository
        )
    }

    @Test
    fun getPartnerReportExpenditureCosts() {
        val LUMP_SUM_ID = 808L
        val UNIT_COST_ID = 809L
        val INVESTMENT_ID = 810L
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 60L
        every { report.number } returns 61
        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { lumpSum.id } returns LUMP_SUM_ID
        every { unitCost.id } returns UNIT_COST_ID
        every { investment.id } returns INVESTMENT_ID
        val expenditure =
            dummyExpenditure(id = 14L, report, lumpSum, unitCost, investment, dummyExpenditure(id = 3L, report))
        every {
            reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
                reportId = 44L,
                partnerId = PARTNER_ID,
            )
        } returns mutableListOf(expenditure)

        assertThat(persistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 44L))
            .containsExactly(
                dummyExpectedExpenditure(id = 14L, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID, 1)
                    .copy(
                        contractId = PROCUREMENT_ID,
                        parkingMetadata = ExpenditureParkingMetadata(
                            reportOfOriginId = 60L,
                            reportOfOriginNumber = 61,
                            reportProjectOfOriginId = null,
                            originalExpenditureNumber = 14,
                            parkedFromExpenditureId = 14L,
                            parkedOn = null
                        )
                    )
            )
    }

    @Test
    fun `getPartnerReportExpenditureCosts  - pageable`() {
        val LUMP_SUM_ID = 828L
        val PRO_LUMP_SUM_ID = 8281L
        val UNIT_COST_ID = 829L
        val PRO_UNIT_COST_ID = 8291L
        val INVESTMENT_ID = 830L
        val PRO_INVESTMENT_ID = 8301L
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 80L
        every { report.number } returns 81

        val proLumpSum = mockk<ProgrammeLumpSumEntity>()
        every { proLumpSum.id } returns PRO_LUMP_SUM_ID
        every { proLumpSum.translatedValues } returns mutableSetOf(
            ProgrammeLumpSumTranslEntity(
                ProgrammeLumpSumTranslId(PRO_LUMP_SUM_ID, SystemLanguage.EN), "name ls"
            )
        )
        val proUnitCost = mockk<ProgrammeUnitCostEntity>()
        every { proUnitCost.id } returns PRO_UNIT_COST_ID
        every { proUnitCost.translatedValues } returns mutableSetOf(
            ProgrammeUnitCostTranslEntity(
                ProgrammeUnitCostTranslId(PRO_UNIT_COST_ID, SystemLanguage.EN), "name uc"
            )
        )

        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { lumpSum.id } returns LUMP_SUM_ID
        every { lumpSum.orderNr } returns 4
        every { lumpSum.programmeLumpSum } returns proLumpSum
        every { unitCost.id } returns UNIT_COST_ID
        every { unitCost.programmeUnitCost } returns proUnitCost
        every { investment.id } returns INVESTMENT_ID
        every { investment.investmentId } returns PRO_INVESTMENT_ID
        every { investment.workPackageNumber } returns 14
        every { investment.investmentNumber } returns 11

        val expenditure =
            dummyExpenditure(id = 14L, report, lumpSum, unitCost, investment, dummyExpenditure(id = 3L, report))
        every { reportExpenditureRepository.findAllByIdIn(setOf(14L), Pageable.unpaged()) } returns PageImpl(
            listOf(
                expenditure
            )
        )

        assertThat(persistence.getPartnerReportExpenditureCosts(setOf(14L), Pageable.unpaged()))
            .containsExactly(dummyExpectedParkedExpenditure())
    }

    @Test
    fun existsByExpenditureId() {
        every {
            reportExpenditureRepository.existsByPartnerReportPartnerIdAndPartnerReportIdAndId(
                PARTNER_ID, reportId = 18L, 45L
            )
        } returns false
        assertThat(persistence.existsByExpenditureId(PARTNER_ID, reportId = 18L, 45L)).isFalse
    }

    @Test
    fun getExpenditureAttachment() {
        val time = ZonedDateTime.now()
        val attachment = JemsFileMetadataEntity(
            id = 248L,
            projectId = 14L,
            partnerId = 19L,
            path = "path",
            minioBucket = "file-bucket",
            minioLocation = "/sample/location",
            name = "powerpoint.pptx",
            type = JemsFileType.Expenditure,
            size = 324L,
            user = UserEntity(
                id = 210L,
                "email",
                name = "name",
                surname = "surname",
                sendNotificationsToEmail = false,
                userRole = mockk(),
                password = "",
                userStatus = UserStatus.ACTIVE
            ),
            uploaded = time,
            description = "desc",
        )
        val parked = mockk<PartnerReportParkedExpenditureEntity>()
        every { parked.parkedFrom.attachment } returns attachment

        every {
            reportExpenditureParkedRepository.findParkedExpenditure(PARTNER_ID, 46L)
        } returns parked

        assertThat(persistence.getExpenditureAttachment(PARTNER_ID, 46L)).isEqualTo(
            JemsFile(
                id = 248L,
                name = "powerpoint.pptx",
                type = JemsFileType.Expenditure,
                uploaded = time,
                author = UserSimple(id = 210L, "email", name = "name", surname = "surname"),
                size = 324L,
                description = "desc",
                indexedPath = "path"
            )
        )
    }

    @Test
    fun `getExpenditureAttachment - empty`() {
        val parked = mockk<PartnerReportParkedExpenditureEntity>()
        every { parked.parkedFrom.attachment } returns null

        every {
            reportExpenditureParkedRepository.findParkedExpenditure(PARTNER_ID, -1L)
        } returns parked

        assertThat(persistence.getExpenditureAttachment(PARTNER_ID, -1L)).isNull()
    }

    @Test
    fun getAvailableLumpSums() {
        every {
            reportLumpSumRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(
                PARTNER_ID,
                reportId = 20L
            )
        } returns
            mutableListOf(dummyLumpSumEntity(mockk()))
        assertThat(persistence.getAvailableLumpSums(PARTNER_ID, reportId = 20L)).containsExactly(dummyLumpSum)
    }

    @Test
    fun getAvailableUnitCosts() {
        every {
            reportUnitCostRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(
                PARTNER_ID,
                reportId = 20L
            )
        } returns
            mutableListOf(dummyUnitCostEntity(mockk()))
        assertThat(persistence.getAvailableUnitCosts(PARTNER_ID, reportId = 20L)).containsExactly(dummyUnitCost)
    }

    @Test
    fun getAvailableInvestments() {
        every {
            reportInvestmentRepository
                .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(
                    PARTNER_ID,
                    reportId = 20L
                )
        } returns mutableListOf(dummyInvestmentEntity(mockk()))
        assertThat(persistence.getAvailableInvestments(PARTNER_ID, reportId = 20L)).containsExactly(dummyInvestment)
    }

    @Test
    fun getAvailableBudgetOptions() {
        val budgetOptionsEntity = mockk<ReportProjectPartnerExpenditureCostCategoryEntity>()
        every { budgetOptionsEntity.reportEntity } returns mockk()
        every { budgetOptionsEntity.reportEntity.partnerId } returns PARTNER_ID
        every { budgetOptionsEntity.officeAndAdministrationOnStaffCostsFlatRate } returns 20
        every { budgetOptionsEntity.officeAndAdministrationOnDirectCostsFlatRate } returns 22
        every { budgetOptionsEntity.travelAndAccommodationOnStaffCostsFlatRate } returns 8
        every { budgetOptionsEntity.staffCostsFlatRate } returns 1
        every { budgetOptionsEntity.otherCostsOnStaffCostsFlatRate } returns null
        every {
            reportCostCategoriesRepository
                .findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 20L)
        } returns budgetOptionsEntity
        assertThat(persistence.getAvailableBudgetOptions(PARTNER_ID, reportId = 20L)).isEqualTo(dummyBudgetOptions)
    }

    @Test
    fun updatePartnerReportExpenditureCosts() {
        val LUMP_SUM_ID = 708L
        val UNIT_COST_ID = 709L
        val INVESTMENT_ID = 710L
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 75L
        every { report.number } returns 4
        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { lumpSum.id } returns LUMP_SUM_ID
        every { unitCost.id } returns UNIT_COST_ID
        every { investment.id } returns INVESTMENT_ID

        val unparkedFrom = dummyExpenditure(9999L, report)
        val entityToStay = dummyExpenditure(EXPENDITURE_TO_STAY, report, null, null, null, unparkedFrom)
        val entityToDelete = dummyExpenditure(EXPENDITURE_TO_DELETE, report, null, null, null)
        val entityToUpdate = dummyExpenditure(EXPENDITURE_TO_UPDATE, report, lumpSum, unitCost, investment)
        every { reportRepository.findByIdAndPartnerId(id = 58L, PARTNER_ID) } returns report

        every { reportExpenditureRepository.findByPartnerReportIdOrderByIdDesc(58L) } returns
            mutableListOf(entityToStay, entityToDelete, entityToUpdate)

        every { fileRepository.delete(dummyAttachment) } answers { }
        val slotDeleted = slot<Iterable<PartnerReportExpenditureCostEntity>>()
        every { reportExpenditureRepository.deleteAll(capture(slotDeleted)) } answers { }

        every {
            reportLumpSumRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(
                PARTNER_ID,
                reportId = 58L
            )
        } returns
            mutableListOf(lumpSum)

        every {
            reportUnitCostRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(
                PARTNER_ID,
                reportId = 58L
            )
        } returns
            mutableListOf(unitCost)

        every {
            reportInvestmentRepository
                .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(
                    PARTNER_ID,
                    reportId = 58L
                )
        } returns mutableListOf(investment)

        val slotSavedEntities = mutableListOf<PartnerReportExpenditureCostEntity>()
        every { reportExpenditureRepository.save(capture(slotSavedEntities)) } returnsArgument 0

        assertThat(
            persistence.updatePartnerReportExpenditureCosts(
                PARTNER_ID, reportId = 58L, listOf(
                    dummyExpectedExpenditure(id = EXPENDITURE_TO_STAY, null, null, null, 1),
                    dummyExpectedExpenditure(id = EXPENDITURE_TO_UPDATE, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID, 2),
                    dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_1, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID, 3),
                    dummyExpectedExpenditureNew(id = EXPENDITURE_TO_ADD_2, null, null, null, 4),
                )
            )
        ).containsExactly(
            dummyExpectedExpenditure(id = EXPENDITURE_TO_STAY, null, null, null, 1)
                .copy(
                    parkingMetadata = ExpenditureParkingMetadata(
                        reportOfOriginId = 75L,
                        reportOfOriginNumber = 4,
                        reportProjectOfOriginId = null,
                        originalExpenditureNumber = 14,
                        parkedFromExpenditureId = EXPENDITURE_TO_STAY,
                        parkedOn = null
                    )
                ),
            dummyExpectedExpenditure(id = EXPENDITURE_TO_UPDATE, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID, 2)
                .copy(parkingMetadata = null),
            dummyExpectedExpenditureNew(id = 0L /* EXPENDITURE_TO_ADD_1 */, LUMP_SUM_ID, UNIT_COST_ID, INVESTMENT_ID, 3)
                .copy(parkingMetadata = null),
            dummyExpectedExpenditureNew(id = 0L /* EXPENDITURE_TO_ADD_2 */, null, null, null, 4)
                .copy(parkingMetadata = null),
        )

        assertThat(slotDeleted.captured).containsExactly(entityToDelete)
        assertThat(slotSavedEntities.map { it.id }).hasSize(2)

        slotSavedEntities.forEachIndexed { index, it ->
            assertThat(it.reportLumpSum).isEqualTo(if (index == 0) lumpSum else null)
            assertThat(it.reportUnitCost).isEqualTo(if (index == 0) unitCost else null)
            assertThat(it.costCategory).isEqualTo(ReportBudgetCategory.EquipmentCosts)
            assertThat(it.reportInvestment).isEqualTo(if (index == 0) investment else null)
            assertThat(it.procurementId).isEqualTo(PROCUREMENT_ID + 10)
            assertThat(it.internalReferenceNumber).isEqualTo("irn NEW")
            assertThat(it.invoiceNumber).isEqualTo("invoice NEW")
            assertThat(it.invoiceDate).isEqualTo(YESTERDAY.minusDays(1))
            assertThat(it.dateOfPayment).isEqualTo(TOMORROW.plusDays(1))
            assertThat(it.translatedValues.first().comment).isEqualTo("comment EN NEW")
            assertThat(it.translatedValues.first().description).isEqualTo("desc EN NEW")
            assertThat(it.totalValueInvoice).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(it.vat).isEqualByComparingTo(BigDecimal.TEN)
            assertThat(it.numberOfUnits).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(it.pricePerUnit).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(it.declaredAmount).isEqualByComparingTo(BigDecimal.ONE)
        }
    }

    @Test
    fun reIncludeParkedExpenditure() {
        val partnerId = 17L
        val expenditureId = 54L
        val parkedOn = ZonedDateTime.now()

        val reportOfOrigin = mockk<ProjectPartnerReportEntity>()
        every { reportOfOrigin.id } returns 11L
        every { reportOfOrigin.number } returns 111
        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        every { lumpSum.id } returns 65L
        every { lumpSum.orderNr } returns 12
        every { lumpSum.programmeLumpSum.id } returns 636L
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        every { unitCost.id } returns 69L
        every { unitCost.programmeUnitCost.id } returns 637L
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { investment.id } returns 71L
        every { investment.investmentId } returns 638L
        val unParkedFrom = mockk<PartnerReportExpenditureCostEntity>()
        every { unParkedFrom.id } returns expenditureId

        val proLumpSum = mockk<PartnerReportLumpSumEntity>()
        every { proLumpSum.id } returns 636L
        val proUnitCost = mockk<PartnerReportUnitCostEntity>()
        every { proUnitCost.id } returns 637L
        val proInvestment = mockk<PartnerReportInvestmentEntity>()
        every { proInvestment.id } returns 638L

        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 2L
        every { reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = 600L) } returns report

        every {
            reportLumpSumRepository.findByReportEntityIdAndProgrammeLumpSumIdAndOrderNr(
                reportId = 600L,
                636L,
                12
            )
        } returns proLumpSum
        every {
            reportUnitCostRepository.findByReportEntityIdAndProgrammeUnitCostId(
                reportId = 600L,
                637L
            )
        } returns proUnitCost
        every {
            reportInvestmentRepository.findByReportEntityIdAndInvestmentId(
                reportId = 600L,
                638L
            )
        } returns proInvestment

        every {
            reportExpenditureParkedRepository.findParkedExpenditure(partnerId = partnerId, id = expenditureId)
        } returns PartnerReportParkedExpenditureEntity(
            parkedFromExpenditureId = expenditureId,
            parkedInProjectReport = null,
            parkedFrom = parkedFrom(
                report = report,
                lumpSum = lumpSum,
                unitCost = unitCost,
                investment = investment,
                unParkedFrom = unParkedFrom,
                reportOfOrigin = reportOfOrigin,
            ),
            reportOfOrigin = reportOfOrigin,
            originalNumber = 4,
            parkedOn = parkedOn
        )
        every { reportExpenditureRepository.save(any()) } returnsArgument 0

        assertThat(persistence.reIncludeParkedExpenditure(partnerId = partnerId, reportId = 600L, expenditureId))
            .isEqualTo(parkedFromExpected())
    }

    @Test
    fun `reIncludeParkedExpenditure - first time parked`() {
        val partnerId = 18L
        val expenditureId = 55L
        val parkedOn = ZonedDateTime.now()

        val reportOfOrigin = mockk<ProjectPartnerReportEntity>()
        val lumpSum = mockk<PartnerReportLumpSumEntity>()
        every { lumpSum.id } returns 65L
        every { lumpSum.orderNr } returns 4
        every { lumpSum.programmeLumpSum.id } returns 650L
        val unitCost = mockk<PartnerReportUnitCostEntity>()
        every { unitCost.id } returns 69L
        every { unitCost.programmeUnitCost.id } returns 690L
        val investment = mockk<PartnerReportInvestmentEntity>()
        every { investment.id } returns 71L
        every { investment.investmentId } returns 710L
        every { investment.workPackageNumber } returns 7

        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 2L
        every { report.number } returns 21
        every { reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = 600L) } returns report

        every {
            reportExpenditureParkedRepository.findParkedExpenditure(partnerId = partnerId, id = expenditureId)
        } returns PartnerReportParkedExpenditureEntity(
            parkedFromExpenditureId = expenditureId,
            parkedInProjectReport = null,
            parkedFrom = parkedFrom(
                report = report,
                lumpSum = lumpSum,
                unitCost = unitCost,
                investment = investment,
                unParkedFrom = null,
                reportOfOrigin = null,
            ),
            reportOfOrigin = reportOfOrigin,
            originalNumber = 4,
            parkedOn = parkedOn
        )

        val proLumpSum = mockk<PartnerReportLumpSumEntity>()
        every { proLumpSum.id } returns 6500L
        val proUnitCost = mockk<PartnerReportUnitCostEntity>()
        every { proUnitCost.id } returns 6900L
        val proInvestment = mockk<PartnerReportInvestmentEntity>()
        every { proInvestment.id } returns 7100L
        every {
            reportLumpSumRepository.findByReportEntityIdAndProgrammeLumpSumIdAndOrderNr(
                600L,
                650L,
                4
            )
        } returns proLumpSum
        every { reportUnitCostRepository.findByReportEntityIdAndProgrammeUnitCostId(600L, 690L) } returns proUnitCost
        every { reportInvestmentRepository.findByReportEntityIdAndInvestmentId(600L, 710L) } returns proInvestment
        every { reportExpenditureRepository.save(any()) } returnsArgument 0

        assertThat(persistence.reIncludeParkedExpenditure(partnerId = partnerId, reportId = 600L, expenditureId))
            .isEqualTo(
                parkedFromExpected().copy(
                    lumpSumId = 6500L,
                    unitCostId = 6900L,
                    investmentId = 7100L,
                    parkingMetadata = ExpenditureParkingMetadata(
                        reportOfOriginId = 2L,
                        reportOfOriginNumber = 21,
                        reportProjectOfOriginId = null,
                        originalExpenditureNumber = 4,
                        parkedFromExpenditureId = 0,
                        parkedOn = null
                    ),
                )
            )
    }

    @Test
    fun `reIncludeParkedExpenditure - first time parked - without links`() {
        val partnerId = 18L
        val expenditureId = 55L
        val parkedOn = ZonedDateTime.now()

        val reportOfOrigin = mockk<ProjectPartnerReportEntity>()

        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 2L
        every { report.number } returns 21
        every { reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = 600L) } returns report

        every {
            reportExpenditureParkedRepository.findParkedExpenditure(partnerId = partnerId, id = expenditureId)
        } returns PartnerReportParkedExpenditureEntity(
            parkedFromExpenditureId = expenditureId,
            parkedFrom = parkedFrom(
                report = report,
                lumpSum = null,
                unitCost = null,
                investment = null,
                unParkedFrom = null,
                reportOfOrigin = null,
            ),
            reportOfOrigin = reportOfOrigin,
            parkedInProjectReport = null,
            originalNumber = 4,
            parkedOn = parkedOn
        )

        every { reportExpenditureRepository.save(any()) } returnsArgument 0

        assertThat(persistence.reIncludeParkedExpenditure(partnerId = partnerId, reportId = 600L, expenditureId))
            .isEqualTo(
                parkedFromExpected().copy(
                    lumpSumId = null,
                    unitCostId = null,
                    investmentId = null,
                    parkingMetadata = ExpenditureParkingMetadata(
                        reportOfOriginId = 2L,
                        reportOfOriginNumber = 21,
                        reportProjectOfOriginId = null,
                        originalExpenditureNumber = 4,
                        parkedFromExpenditureId = 0,
                        parkedOn = null
                    ),
                )
            )
    }

    @Test
    fun `markAsSampledAndLock for expenditures`() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 60L
        every { report.number } returns 61
        val expenditure = dummyExpenditure(14L, report)
        every { reportExpenditureRepository.findAllById(setOf(14L)) } returns listOf(expenditure)
        every { reportExpenditureRepository.save(any()) } returnsArgument 0
        persistence.markAsSampledAndLock(setOf(14L))
        assertThat(expenditure.partOfSample).isTrue
        assertThat(expenditure.partOfSampleLocked).isTrue
    }

}
