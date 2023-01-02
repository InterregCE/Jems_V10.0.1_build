package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.Runs
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class UpdateProjectPartnerReportExpenditureVerificationTest : UnitTest() {

    private val TODAY = LocalDate.now()


    private val verification = ProjectPartnerReportExpenditureVerification(
        id = 14L,
        number = 1,
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
        certifiedAmount = BigDecimal(50),
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = null,
        parked = false
    )

    private val verificationParked = ProjectPartnerReportExpenditureVerification(
        id = 14L,
        number = 1,
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
        parked = true
    )

    private val existingError = TypologyErrors(
        id = 7L,
        description = "error 7",
    )

    private val expectedUpdateWithoutParking = ExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.TEN,
        deductedAmount = BigDecimal.valueOf(-9),
        typologyOfErrorId = existingError.id,
        verificationComment = "new comment",
        parked = false
    )

    private val expectedUpdateWithParking = ExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.ZERO,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = "parked expenditure",
        parked = true
    )

    private val expenditureUpdateValidWithoutParking = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.TEN,
        typologyOfErrorId = existingError.id,
        verificationComment = "new comment",
        parked = false
    )

    private val expenditureUpdateValidWithParking = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = "parked expenditure",
        parked = true
    )

    private val expenditureUpdateInvalid = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 1L,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(5, 1),
        typologyOfErrorId = null,
        verificationComment = null,
        parked = false
    )

    private val projectPartnerReport = ProjectPartnerReport(
        id = 55L,
        reportNumber = 1,
        status = ReportStatus.InControl,
        version = "v1.0",
        firstSubmission = ZonedDateTime.now(),
        identification = mockk()
    )

    @MockK
    private lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    private lateinit var typologyPersistence: ProgrammeTypologyErrorsPersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerification

    @Test
    fun `updatePartnerReportExpenditureVerification - without parking`() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55L) } returns
            listOf(verification)
        every { typologyPersistence.getAllTypologyErrors() } returns listOf(existingError)

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate))
        } returns listOf(verification)

        assertThat(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55L, listOf(expenditureUpdateValidWithoutParking))
        ).isEqualTo(listOf(verification))

        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithoutParking)
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - with parking`() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55L) } returns
            listOf(verification)
        every { typologyPersistence.getAllTypologyErrors() } returns listOf(existingError)
        every { reportPersistence.getPartnerReportById(17L, 55L) } returns projectPartnerReport
        every { partnerPersistence.getProjectIdForPartnerId(17L, "v1.0") } returns 40L
        every { projectPartnerReport.identification.projectIdentifier } returns "identifier"
        every { projectPartnerReport.identification.projectAcronym } returns "acronym"
        every { projectPartnerReport.identification.partnerRole } returns ProjectPartnerRole.PARTNER
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate))
        } returns listOf(verificationParked)

        assertThat(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55L, listOf(expenditureUpdateValidWithParking))
        ).isEqualTo(listOf(verificationParked))
        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithParking)
        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_EXPENDITURE_PARKED)
        assertThat(auditSlot.captured.auditCandidate.description)
            .isEqualTo("Controller parked the following expenditures: [R1.1] of partner PP from report R.1")
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
