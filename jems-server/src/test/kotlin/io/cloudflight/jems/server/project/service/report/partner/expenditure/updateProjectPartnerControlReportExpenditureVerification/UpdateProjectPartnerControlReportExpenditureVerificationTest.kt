package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectControlReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class UpdateProjectPartnerControlReportExpenditureVerificationTest : UnitTest() {

    private val TODAY = LocalDate.now()
    private val MOMENT = ZonedDateTime.now()

    private val expenditureValid = ProjectPartnerControlReportExpenditureVerification(
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
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(3670),
        deductedAmount = BigDecimal.valueOf(10),
        typologyOfErrorId = 1,
        verificationComment = null
    )

    private val expenditureUpdateValid = ProjectPartnerControlReportExpenditureVerificationUpdate(
        id = 1L,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(3670),
        deductedAmount = BigDecimal.valueOf(10),
        typologyOfErrorId = 1,
        verificationComment = null
    )

    private val expenditureUpdateInvalid = ProjectPartnerControlReportExpenditureVerificationUpdate(
        id = 1L,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(3670),
        deductedAmount = BigDecimal.valueOf(10),
        typologyOfErrorId = null,
        verificationComment = null
    )

    @MockK
    lateinit var reportExpenditurePersistence: ProjectControlReportExpenditurePersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerification

    @Test
    fun updateExpenditureVerification() {
        every {
            reportExpenditurePersistence.updatePartnerControlReportExpenditureVerification(
                1L,
                1L,
                listOf(expenditureUpdateValid)
            )
        } returns listOf(expenditureValid)

        Assertions.assertThat(
            updatePartnerReportExpenditureVerification.updatePartnerReportExpenditureVerification(
                1L,
                reportId = 1L,
                listOf(expenditureUpdateValid)
            )
        ).containsExactly(expenditureValid)
    }

    @Test
    fun updateExpenditureVerificationWithNullTopologyId() {
        assertThrows<TypologyOfErrorsIdIsNullException> {
            updatePartnerReportExpenditureVerification.updatePartnerReportExpenditureVerification(
                1L,
                reportId = 1L,
                listOf(expenditureUpdateInvalid)
            )
        }
    }
}
