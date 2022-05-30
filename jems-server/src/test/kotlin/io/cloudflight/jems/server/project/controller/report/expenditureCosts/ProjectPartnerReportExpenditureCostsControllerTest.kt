package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport.GetAvailableLumpSumsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableUnitCostsForReport.GetAvailableUnitCostsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure.UpdateProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.UploadFileToProjectPartnerReportExpenditure
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
        attachment = ProjectReportFileMetadata(500L, "file.txt", UPLOADED),
    )

    private val reportExpenditureCostDto = ProjectPartnerReportExpenditureCostDTO(
        id = 754,
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
    )

    private val stream = ByteArray(5).inputStream()

    private val dummyFile = ProjectReportFileMetadata(id = 90L, "file_name.ext", uploaded = UPLOADED)
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
        costPerUnit = BigDecimal.ONE,
        numberOfUnits = BigDecimal.ONE,
        total = BigDecimal.ONE,
        costPerUnitForeignCurrency = BigDecimal.TEN,
        foreignCurrencyCode = "RON",
        name = setOf(InputTranslation(SystemLanguage.EN, "EN unit cost")),
        category = ReportBudgetCategory.Multiple
    )

    private val dummyUnitCostDTO = ProjectPartnerReportUnitCostDTO(
        id = 18L,
        unitCostProgrammeId = 140L,
        costPerUnit = BigDecimal.ONE,
        numberOfUnits = BigDecimal.ONE,
        total = BigDecimal.ONE,
        costPerUnitForeignCurrency = BigDecimal.TEN,
        foreignCurrencyCode = "RON",
        name = setOf(InputTranslation(SystemLanguage.EN, "EN unit cost")),
        category = BudgetCategoryDTO.Multiple
    )

    @MockK
    lateinit var getProjectPartnerReportExpenditureInteractor: GetProjectPartnerReportExpenditureInteractor

    @MockK
    lateinit var updateProjectPartnerReportExpenditureInteractor: UpdateProjectPartnerReportExpenditureInteractor

    @MockK
    lateinit var uploadFileToExpenditure: UploadFileToProjectPartnerReportExpenditure

    @MockK
    lateinit var getAvailableLumpSumsForReportInteractor: GetAvailableLumpSumsForReportInteractor

    @MockK
    lateinit var getAvailableUnitCostsForReportInteractor: GetAvailableUnitCostsForReportInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportExpenditureCostsController

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

}
