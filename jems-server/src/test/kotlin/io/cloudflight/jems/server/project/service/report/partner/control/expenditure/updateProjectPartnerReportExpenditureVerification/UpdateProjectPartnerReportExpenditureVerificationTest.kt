package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

internal class UpdateProjectPartnerReportExpenditureVerificationTest : UnitTest() {

    private val TODAY = LocalDate.now()

    private val verification = ProjectPartnerReportExpenditureVerification(
        id = 14L,
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
        declaredAmount = BigDecimal.ZERO,
        currencyCode = "CST",
        currencyConversionRate = BigDecimal.TEN,
        declaredAmountAfterSubmission = BigDecimal.ONE,
        attachment = mockk(),

        partOfSample = false,
        certifiedAmount = BigDecimal.ZERO,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = null,
    )

    private val existingError = TypologyErrors(
        id = 7L,
        description = "error 7",
    )

    private val expectedUpdate = ExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.TEN,
        deductedAmount = BigDecimal.valueOf(-9),
        typologyOfErrorId = existingError.id,
        verificationComment = "new comment",
    )

    private val expenditureUpdateValid = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.TEN,
        typologyOfErrorId = existingError.id,
        verificationComment = "new comment"
    )

    private val expenditureUpdateInvalid = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 1L,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(5, 1),
        typologyOfErrorId = null,
        verificationComment = null
    )

    @MockK
    private lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence
    @MockK
    private lateinit var typologyPersistence: ProgrammeTypologyErrorsPersistence

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerification

    @Test
    fun updatePartnerReportExpenditureVerification() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55L) } returns
            listOf(verification)
        every { typologyPersistence.getAllTypologyErrors() } returns listOf(existingError)

        val result = mockk<List<ProjectPartnerReportExpenditureVerification>>()
        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate))
        } returns result

        assertThat(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55L, listOf(expenditureUpdateValid))
        ).isEqualTo(result)

        assertThat(slotToUpdate.captured).containsExactly(expectedUpdate)
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - topology error`() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 11L, reportId = 4L) } returns
            listOf(verification.copy(id = 1L))
        every { typologyPersistence.getAllTypologyErrors() } returns emptyList()

        assertThrows<TypologyOfErrorMissing> {
            updatePartnerReportExpenditureVerification
                .updatePartnerReportExpenditureVerification(partnerId = 11L, reportId = 4L, listOf(expenditureUpdateInvalid))
        }
    }

}
