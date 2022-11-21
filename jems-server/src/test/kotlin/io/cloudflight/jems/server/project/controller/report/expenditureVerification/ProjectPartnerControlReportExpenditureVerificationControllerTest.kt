package io.cloudflight.jems.server.project.controller.report.expenditureVerification

import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerControlReportExpenditureVerification.GetProjectPartnerControlReportExpenditureVerificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerControlReportExpenditureVerification.UpdateProjectPartnerControlReportExpenditureVerificationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectPartnerControlReportExpenditureVerificationControllerTest : UnitTest() {
    private val PARTNER_ID = 11L
    private val CONTRACT_ID = 17L
    private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

    private val reportExpenditureVerification = ProjectPartnerControlReportExpenditureVerification(
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
        attachment = JemsFileMetadata(500L, "file.txt", UPLOADED),
        partOfSample = false,
        certifiedAmount = BigDecimal.valueOf(1.3),
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = null
    )

    private val reportExpenditureVerificationUpdated = ProjectPartnerControlReportExpenditureVerification(
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
        attachment = JemsFileMetadata(500L, "file.txt", UPLOADED),
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(1),
        deductedAmount = BigDecimal.valueOf(0.3),
        typologyOfErrorId = 1,
        verificationComment = "test"
    )

    private val reportExpenditureVerificationDto = ProjectPartnerControlReportExpenditureVerificationDTO(
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
        partOfSample = false,
        certifiedAmount = BigDecimal.valueOf(1.3),
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = null
    )

    private val reportExpenditureVerificationDtoUpdated = ProjectPartnerControlReportExpenditureVerificationDTO(
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
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(1),
        deductedAmount = BigDecimal.valueOf(0.3),
        typologyOfErrorId = 1,
        verificationComment = "test"
    )

    private val reportExpenditureVerificationUpdate = ProjectPartnerControlReportExpenditureVerificationUpdateDTO(
        id = 754,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(1),
        deductedAmount = BigDecimal.valueOf(0.3),
        typologyOfErrorId = 1,
        verificationComment = "test"
    )

    @MockK
    lateinit var getProjectPartnerControlReportExpenditureVerificationInteractor: GetProjectPartnerControlReportExpenditureVerificationInteractor

    @MockK
    lateinit var updateProjectPartnerControlReportExpenditureVerificationInteractor: UpdateProjectPartnerControlReportExpenditureVerificationInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerControlReportExpenditureVerificationController

    @Test
    fun getProjectPartnerReportExpenditureVerification() {
        every { getProjectPartnerControlReportExpenditureVerificationInteractor.getExpenditureVerification(PARTNER_ID, reportId = 17L) } returns
            listOf(reportExpenditureVerification)
        Assertions.assertThat(controller.getProjectPartnerExpenditureVerification(PARTNER_ID, reportId = 17L))
            .containsExactly(reportExpenditureVerificationDto)
    }

    @Test
    fun updatePartnerReportExpenditureVerification() {
        val slotData = slot<List<ProjectPartnerControlReportExpenditureVerificationUpdate>>()

        every { updateProjectPartnerControlReportExpenditureVerificationInteractor.updatePartnerReportExpenditureVerification(
            partnerId = PARTNER_ID,
            reportId = 20L,
            capture(slotData),
        ) } returns listOf(reportExpenditureVerificationUpdated)

        Assertions.assertThat(
            controller.updatePartnerReportExpendituresVerification(
                PARTNER_ID,
                reportId = 20L,
                listOf(reportExpenditureVerificationUpdate)
            )
        )
            .containsExactly(reportExpenditureVerificationDtoUpdated)

        Assertions.assertThat(slotData.captured).hasSize(1)
    }
}
