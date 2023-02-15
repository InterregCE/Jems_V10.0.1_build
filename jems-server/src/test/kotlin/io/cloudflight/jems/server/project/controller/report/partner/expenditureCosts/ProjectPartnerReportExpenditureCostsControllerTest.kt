package io.cloudflight.jems.server.project.controller.report.partner.expenditureCosts

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportInvestmentDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportParkedExpenditureDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportParkedLinkedDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ExpenditureParkingMetadataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedLinked
import io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure.DeleteParkedExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableBudgetOptionsForReport.GetAvailableBudgetOptionsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableInvestmentsForReport.GetAvailableInvestmentsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport.GetAvailableLumpSumsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList.GetAvailableParkedExpenditureListInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableUnitCostsForReport.GetAvailableUnitCostsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure.ReIncludeParkedExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure.UpdateProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.UploadFileToProjectPartnerReportExpenditure
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ProjectPartnerReportExpenditureCostsControllerTest : UnitTest() {

    private val PARTNER_ID = 11L
    private val CONTRACT_ID = 17L
    private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

    private val reportExpenditureCost = ProjectPartnerReportExpenditureCost(
        id = 754,
        number = 1,
        lumpSumId = null,
        unitCostId = null,
        costCategory = ReportBudgetCategory.ExternalCosts,
        investmentId = 10L,
        contractId = CONTRACT_ID,
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
        parkingMetadata = ExpenditureParkingMetadata(reportOfOriginId = 14L, reportOfOriginNumber = 2, originalExpenditureNumber = 9),
    )

    private val reportParkedExpenditure = ProjectPartnerReportParkedExpenditure(
        expenditure = reportExpenditureCost,
        lumpSum = ProjectPartnerReportParkedLinked(51L, 52L, 4, true),
        lumpSumName = setOf(InputTranslation(SystemLanguage.EN, "ls-name")),
        unitCost = ProjectPartnerReportParkedLinked(61L, 62L, null, false),
        unitCostName = setOf(InputTranslation(SystemLanguage.EN, "uc-name")),
        investment = ProjectPartnerReportParkedLinked(71L, 72L, null, true),
        investmentName = "investment-name",
    )

    private val reportExpenditureCostDto = ProjectPartnerReportExpenditureCostDTO(
        id = 754,
        number = 1,
        lumpSumId = null,
        unitCostId = null,
        costCategory = BudgetCategoryDTO.ExternalCosts,
        investmentId = 10L,
        contractId = CONTRACT_ID,
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
        attachment = ProjectReportFileMetadataDTO(500L, "file.txt", UPLOADED),
        parkingMetadata = ExpenditureParkingMetadataDTO(reportOfOriginId = 14L, reportOfOriginNumber = 2, originalExpenditureNumber = 9),
    )

    private val reportParkedExpenditureDto = ProjectPartnerReportParkedExpenditureDTO(
        expenditure = reportExpenditureCostDto,
        lumpSum = ProjectPartnerReportParkedLinkedDTO(51L, 52L, true),
        lumpSumName = setOf(InputTranslation(SystemLanguage.EN, "ls-name")),
        unitCost = ProjectPartnerReportParkedLinkedDTO(61L, 62L, false),
        unitCostName = setOf(InputTranslation(SystemLanguage.EN, "uc-name")),
        investment = ProjectPartnerReportParkedLinkedDTO(71L, 72L, true),
        investmentName = "investment-name",
    )

    private val stream = ByteArray(5).inputStream()

    private val dummyFile = JemsFileMetadata(id = 90L, "file_name.ext", uploaded = UPLOADED)
    private val dummyFileDto = ProjectReportFileMetadataDTO(id = 90L, "file_name.ext", uploaded = UPLOADED)
    private val dummyFileExpected = ProjectFile(stream, "file_name.ext", 50L)
    private fun dummyMultipartFile(name: String = "file_name.ext", originalName: String? = null): MultipartFile {
        val file = mockk<MultipartFile>()
        every { file.inputStream } returns stream
        every { file.originalFilename } returns originalName
        every { file.name } returns name
        every { file.size } returns 50L
        return file
    }

    private val dummyLumpSum = ProjectPartnerReportLumpSum(
        id = 18L,
        lumpSumProgrammeId = 140L,
        fastTrack = false,
        orderNr = 8,
        period = 4,
        cost = BigDecimal.ONE,
        name = setOf(InputTranslation(SystemLanguage.EN, "EN lump sum"))
    )

    private val dummyLumpSumDto = ProjectPartnerReportLumpSumDTO(
        id = 18L,
        lumpSumProgrammeId = 140L,
        period = 4,
        cost = BigDecimal.ONE,
        name = setOf(InputTranslation(SystemLanguage.EN, "EN lump sum"))
    )

    private val dummyUnitCost = ProjectPartnerReportUnitCost(
        id = 18L,
        unitCostProgrammeId = 140L,
        projectDefined = true,
        costPerUnit = BigDecimal.ONE,
        numberOfUnits = BigDecimal.ONE,
        total = BigDecimal.ONE,
        costPerUnitForeignCurrency = BigDecimal.TEN,
        foreignCurrencyCode = "RON",
        name = setOf(InputTranslation(SystemLanguage.EN, "EN unit cost")),
        category = ReportBudgetCategory.Multiple
    )

    private val dummyInvestment = ProjectPartnerReportInvestment(
        id = 256L,
        investmentId = 678L,
        workPackageNumber = 6,
        investmentNumber = 4,
        title = setOf(InputTranslation(SystemLanguage.EN, "EN investment")),
        total = BigDecimal.ONE,
        deactivated = true,
    )

    private val dummyUnitCostDTO = ProjectPartnerReportUnitCostDTO(
        id = 18L,
        unitCostProgrammeId = 140L,
        projectDefined = true,
        costPerUnit = BigDecimal.ONE,
        numberOfUnits = BigDecimal.ONE,
        total = BigDecimal.ONE,
        costPerUnitForeignCurrency = BigDecimal.TEN,
        foreignCurrencyCode = "RON",
        name = setOf(InputTranslation(SystemLanguage.EN, "EN unit cost")),
        category = BudgetCategoryDTO.Multiple
    )

    private val dummyInvestmentDTO = ProjectPartnerReportInvestmentDTO(
        id = 256L,
        investmentId = 678L,
        workPackageNumber = 6,
        investmentNumber = 4,
        title = setOf(InputTranslation(SystemLanguage.EN, "EN investment")),
        deactivated = true,
    )

    private val dummyBudgetOptions = ProjectPartnerBudgetOptions(
        officeAndAdministrationOnStaffCostsFlatRate = 20,
        officeAndAdministrationOnDirectCostsFlatRate = 22,
        travelAndAccommodationOnStaffCostsFlatRate = 8,
        staffCostsFlatRate = 1,
        partnerId = PARTNER_ID
    )

    private val dummyBudgetOptionsDto = ProjectPartnerBudgetOptionsDto(
        officeAndAdministrationOnStaffCostsFlatRate = 20,
        officeAndAdministrationOnDirectCostsFlatRate = 22,
        travelAndAccommodationOnStaffCostsFlatRate = 8,
        staffCostsFlatRate = 1,
    )

    @MockK
    private lateinit var getProjectPartnerReportExpenditureInteractor: GetProjectPartnerReportExpenditureInteractor

    @MockK
    private lateinit var updateProjectPartnerReportExpenditureInteractor: UpdateProjectPartnerReportExpenditureInteractor

    @MockK
    private lateinit var uploadFileToExpenditure: UploadFileToProjectPartnerReportExpenditure

    @MockK
    private lateinit var getAvailableLumpSumsForReportInteractor: GetAvailableLumpSumsForReportInteractor

    @MockK
    private lateinit var getAvailableUnitCostsForReportInteractor: GetAvailableUnitCostsForReportInteractor

    @MockK
    private lateinit var getAvailableInvestmentsForReportInteractor: GetAvailableInvestmentsForReportInteractor

    @MockK
    private lateinit var getAvailableBudgetOptionsForReportInteractor: GetAvailableBudgetOptionsForReportInteractor

    @MockK
    private lateinit var getAvailableParkedExpenditureListInteractor: GetAvailableParkedExpenditureListInteractor

    @MockK
    private lateinit var reIncludeParkedExpenditureInteractor: ReIncludeParkedExpenditureInteractor

    @MockK
    private lateinit var deleteParkedExpenditureInteractor: DeleteParkedExpenditureInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportExpenditureCostsController

    @BeforeEach
    fun reset() {
        clearMocks(reIncludeParkedExpenditureInteractor, deleteParkedExpenditureInteractor)
    }

    @Test
    fun getProjectPartnerReports() {
        every { getProjectPartnerReportExpenditureInteractor.getExpenditureCosts(PARTNER_ID, reportId = 17L) } returns
            listOf(reportExpenditureCost)
        assertThat(controller.getProjectPartnerReports(PARTNER_ID, reportId = 17L))
            .containsExactly(reportExpenditureCostDto)
    }

    @Test
    fun updatePartnerReportExpenditures() {
        val slotData = slot<List<ProjectPartnerReportExpenditureCost>>()

        every { updateProjectPartnerReportExpenditureInteractor.updatePartnerReportExpenditureCosts(
            partnerId = PARTNER_ID,
            reportId = 20L,
            capture(slotData),
        ) } returns listOf(reportExpenditureCost)

        assertThat(controller.updatePartnerReportExpenditures(PARTNER_ID, reportId = 20L, listOf(reportExpenditureCostDto)))
            .containsExactly(reportExpenditureCostDto)

        assertThat(slotData.captured).hasSize(1)
        assertThat(slotData.captured.first()).isEqualTo(reportExpenditureCost)
    }

    @Test
    fun uploadFileToExpenditure() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToExpenditure.uploadToExpenditure(PARTNER_ID, reportId = 35L, 75L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToExpenditure(PARTNER_ID, 35L, 75L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun getAvailableLumpSums() {
        every { getAvailableLumpSumsForReportInteractor.getLumpSums(PARTNER_ID, 38L) } returns
            listOf(dummyLumpSum)
        assertThat(controller.getAvailableLumpSums(PARTNER_ID, reportId = 38L)).containsExactly(dummyLumpSumDto)
    }

    @Test
    fun getAvailableUnitCosts() {
        every { getAvailableUnitCostsForReportInteractor.getUnitCosts(PARTNER_ID, 38L) } returns
            listOf(dummyUnitCost)
        assertThat(controller.getAvailableUnitCosts(PARTNER_ID, reportId = 38L)).containsExactly(dummyUnitCostDTO)
    }

    @Test
    fun getAvailableInvestments() {
        every { getAvailableInvestmentsForReportInteractor.getInvestments(PARTNER_ID, 38L) } returns
            listOf(dummyInvestment)
        assertThat(controller.getAvailableInvestments(PARTNER_ID, reportId = 38L)).containsExactly(dummyInvestmentDTO)
    }

    @Test
    fun getAvailableBudgetOptions() {
        every { getAvailableBudgetOptionsForReportInteractor.getBudgetOptions(PARTNER_ID, 38L) } returns
            dummyBudgetOptions
        assertThat(controller.getAvailableBudgetOptions(PARTNER_ID, reportId = 38L)).isEqualTo(dummyBudgetOptionsDto)
    }

    @Test
    fun getAvailableParkedExpenditures() {
        every { getAvailableParkedExpenditureListInteractor.getParked(PARTNER_ID, reportId = 41L, Pageable.unpaged()) } returns
            PageImpl(listOf(reportParkedExpenditure))
        assertThat(controller.getAvailableParkedExpenditures(PARTNER_ID, 41L, Pageable.unpaged()))
            .containsExactly(reportParkedExpenditureDto)
    }

    @Test
    fun reIncludeParkedExpenditure() {
        every { reIncludeParkedExpenditureInteractor.reIncludeParkedExpenditure(
            partnerId = PARTNER_ID, reportId = 39L, expenditureId = 150L
        ) } answers { }
        controller.reIncludeParkedExpenditure(partnerId = PARTNER_ID, reportId = 39L, expenditureId = 150L)
        verify(exactly = 1) { reIncludeParkedExpenditureInteractor.reIncludeParkedExpenditure(PARTNER_ID, 39L, 150L) }
    }

    @Test
    fun deleteParkedExpenditure() {
        every { deleteParkedExpenditureInteractor.deleteParkedExpenditure(
            partnerId = PARTNER_ID, reportId = 37L, expenditureId = 150L
        ) } answers { }
        controller.deleteParkedExpenditure(partnerId = PARTNER_ID,  reportId = 37L, expenditureId = 150L)
        verify(exactly = 1) { deleteParkedExpenditureInteractor.deleteParkedExpenditure(PARTNER_ID, 37L, 150L) }
    }

}
