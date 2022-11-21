package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectControlReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class GetProjectPartnerControlReportExpenditureVerificationTest : UnitTest() {

    private val TODAY = LocalDate.now()
    private val MOMENT = ZonedDateTime.now()

    private val expenditure = ProjectPartnerControlReportExpenditureVerification(
        id = 1L,
        lumpSumId = 45L,
        unitCostId = 46L,
        costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
        investmentId = 89L,
        contractId = 54L,
        internalReferenceNumber = "145",
        invoiceNumber = "1",
        invoiceDate = TODAY,
        dateOfPayment = TODAY,
        numberOfUnits = BigDecimal.ONE,
        pricePerUnit = BigDecimal.ZERO,
        declaredAmount = BigDecimal.valueOf(2431, 2),
        currencyCode = "CST",
        currencyConversionRate = BigDecimal.valueOf(24302, 4),
        declaredAmountAfterSubmission = BigDecimal.valueOf(1000, 2),
        attachment = JemsFileMetadata(45L, "file.txt", MOMENT),
        partOfSample = false,
        certifiedAmount = BigDecimal.valueOf(3680),
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = null
    )

    @MockK
    lateinit var reportExpenditurePersistence: ProjectControlReportExpenditurePersistence

    @InjectMockKs
    lateinit var getExpenditure: GetProjectPartnerControlReportExpenditureVerification

    @Test
    fun getExpenditureVerification() {
        every {
            reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(1L, 1L)
        } returns listOf(expenditure)

        assertThat(
            getExpenditure.getExpenditureVerification(
                1L,
                reportId = 1L
            )
        ).containsExactly(expenditure)
    }
}
