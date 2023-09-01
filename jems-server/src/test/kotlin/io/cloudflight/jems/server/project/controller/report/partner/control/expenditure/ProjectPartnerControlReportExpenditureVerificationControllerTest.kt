package io.cloudflight.jems.server.project.controller.report.partner.control.expenditure

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ExpenditureParkingMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification.GetProjectPartnerControlReportExpenditureVerificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportParkedExpenditureIds.GetProjectPartnerControlReportParkedExpendituresInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification.UpdateProjectPartnerControlReportExpenditureVerificationInteractor
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

    private val reportExpenditureVerification = ProjectPartnerReportExpenditureVerification(
        id = 754,
        number = 1,
        lumpSumId = 2L,
        unitCostId = null,
        costCategory = ReportBudgetCategory.ExternalCosts,
        gdpr = false,
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
        typologyOfErrorId = 15L,
        verificationComment = "comment dummy",
        parked = false,
        parkedOn = null,
        parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 3
        ),
        partOfSampleLocked = false
    )

    private val reportExpenditureVerificationUpdated = ProjectPartnerReportExpenditureVerification(
        id = 754,
        number = 1,
        lumpSumId = null,
        unitCostId = null,
        costCategory = ReportBudgetCategory.ExternalCosts,
        gdpr = false,
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
        verificationComment = "test",
        parked = false,
        parkedOn = null,
        parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 72L,
            reportOfOriginNumber = 4,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 3
        ),
        partOfSampleLocked = false
    )

    private val reportExpenditureVerificationDto = ProjectPartnerControlReportExpenditureVerificationDTO(
        id = 754,
        number = 1,
        lumpSumId = 2L,
        unitCostId = null,
        costCategory = BudgetCategoryDTO.ExternalCosts,
        gdpr = false,
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
        attachment = JemsFileMetadataDTO(500L, "file.txt", UPLOADED),
        partOfSample = false,
        certifiedAmount = BigDecimal.valueOf(1.3),
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = 15L,
        verificationComment = "comment dummy",
        parked = false,
        parkedOn = null,
        parkingMetadata = ExpenditureParkingMetadataDTO(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 3
        ),
        partOfSampleLocked = false
    )

    private val reportExpenditureVerificationDtoUpdated = ProjectPartnerControlReportExpenditureVerificationDTO(
        id = 754,
        number = 1,
        lumpSumId = null,
        unitCostId = null,
        costCategory = BudgetCategoryDTO.ExternalCosts,
        gdpr = false,
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
        attachment = JemsFileMetadataDTO(500L, "file.txt", UPLOADED),
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(1),
        deductedAmount = BigDecimal.valueOf(0.3),
        typologyOfErrorId = 1,
        verificationComment = "test",
        parked = false,
        parkedOn = null,
        parkingMetadata = ExpenditureParkingMetadataDTO(
            reportOfOriginId = 72L,
            reportOfOriginNumber = 4,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 3
        ),
        partOfSampleLocked = false
    )

    private val toUpdateDto = ProjectPartnerControlReportExpenditureVerificationUpdateDTO(
        id = 754,
        partOfSample = true,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = 1,
        verificationComment = "test",
        parked = false
    )

    private val toUpdate = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 754,
        partOfSample = true,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = 1,
        verificationComment = "test",
        parked = false
    )

    @MockK
    private lateinit var getReportExpenditureVerification: GetProjectPartnerControlReportExpenditureVerificationInteractor

    @MockK
    private lateinit var getControlReportParkedIds: GetProjectPartnerControlReportParkedExpendituresInteractor

    @MockK
    private lateinit var updateReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerificationInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerControlReportExpenditureVerificationController

    @Test
    fun getProjectPartnerReportExpenditureVerification() {
        every { getReportExpenditureVerification.getExpenditureVerification(PARTNER_ID, reportId = 17L) } returns
            listOf(reportExpenditureVerification)
        Assertions.assertThat(controller.getProjectPartnerExpenditureVerification(PARTNER_ID, reportId = 17L))
            .containsExactly(reportExpenditureVerificationDto)
    }

    @Test
    fun updatePartnerReportExpenditureVerification() {
        val slotData = slot<List<ProjectPartnerReportExpenditureVerificationUpdate>>()

        every {
            updateReportExpenditureVerification.updatePartnerReportExpenditureVerification(
                partnerId = PARTNER_ID,
                reportId = 20L,
                capture(slotData),
            )
        } returns listOf(reportExpenditureVerificationUpdated)

        Assertions.assertThat(
            controller.updatePartnerReportExpendituresVerification(
                partnerId = PARTNER_ID,
                reportId = 20L,
                expenditureVerification = listOf(toUpdateDto),
            )
        ).containsExactly(reportExpenditureVerificationDtoUpdated)

        Assertions.assertThat(slotData.captured).containsExactly(toUpdate)
    }

    @Test
    fun getParkedExpenditureIds() {
        val reportParkedIdsVerification = listOf(1L, 3L, 4L, 5L, 8L)

        every {
            getControlReportParkedIds.getParkedExpenditureIds(
                partnerId = PARTNER_ID,
                reportId = 17L
            )
        } returns reportParkedIdsVerification

        Assertions.assertThat(
            controller.getParkedExpenditureIds(
                partnerId = PARTNER_ID,
                reportId = 17L
            )
        ).isEqualTo(reportParkedIdsVerification)
    }
}
