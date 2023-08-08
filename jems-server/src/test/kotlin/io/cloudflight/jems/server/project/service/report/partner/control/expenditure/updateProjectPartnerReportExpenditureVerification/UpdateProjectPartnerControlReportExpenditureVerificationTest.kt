package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class UpdateProjectPartnerControlReportExpenditureVerificationTest : UnitTest() {

    private val TODAY = LocalDate.now()

    private val verification = ProjectPartnerReportExpenditureVerification(
        id = 14L,
        number = 1,
        lumpSumId = 45L,
        unitCostId = 46L,
        costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
        gdpr = false,
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
        parked = false,
        parkedOn = null,
        parkingMetadata = null,
        partOfSampleLocked = false
    )

    private val verificationParked = ProjectPartnerReportExpenditureVerification(
        id = 14L,
        number = 1,
        lumpSumId = 45L,
        unitCostId = 46L,
        costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
        gdpr = false,
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
        parked = true,
        parkedOn = null,
        parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 14L,
            reportOfOriginNumber = 2,
            reportProjectOfOriginId = null,
            originalExpenditureNumber = 9
        ),
        partOfSampleLocked = false
    )

    private val verificationUnParked = verificationParked.copy(parked = false)

    private val existingError = TypologyErrors(
        id = 7L,
        description = "error 7",
    )

    private val expectedUpdateWithoutParking = ExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.ONE,
        deductedAmount = BigDecimal.ZERO,
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

    private val expectedUpdateWithDeduction = ExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(500),
        deductedAmount = BigDecimal.valueOf(200),
        typologyOfErrorId = existingError.id,
        verificationComment = "deduction included",
        parked = false
    )

    private val expectedUpdateWithoutSampled = ExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        certifiedAmount = BigDecimal.valueOf(1),
        typologyOfErrorId = existingError.id,
        verificationComment = "deduction included",
        parked = false,
        deductedAmount = BigDecimal.valueOf(0),
    )

    private val expenditureUpdateValidWithoutParking = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = existingError.id,
        verificationComment = "new comment",
        parked = false
    )

    private val expenditureUpdateValidWithParking = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        verificationComment = "parked expenditure",
        parked = true
    )

    private val expenditureUpdateValidWithDeduction = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = true,
        deductedAmount = BigDecimal.valueOf(200),
        typologyOfErrorId = existingError.id,
        verificationComment = "deduction included",
        parked = false
    )

    private val expenditureUpdateInvalid = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 1L,
        partOfSample = true,
        deductedAmount = BigDecimal.ONE, // required to result an invalid TypologyError
        typologyOfErrorId = null,
        verificationComment = null,
        parked = false
    )

    private val expenditureUpdateWithoutSampled = ProjectPartnerReportExpenditureVerificationUpdate(
        id = 14L,
        partOfSample = false,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = existingError.id,
        verificationComment = "deduction included",
        parked = false
    )

    private val projectPartnerReport = ProjectPartnerReport(
        id = 55L,
        reportNumber = 4,
        status = ReportStatus.InControl,
        version = "v1.0",
        firstSubmission = ZonedDateTime.now(),
        identification = mockk(),
        controlEnd = null,
        lastResubmission = null,
        lastControlReopening = null,
        projectReportId = 44L,
        projectReportNumber = 440,
    )

    @MockK
    private lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    private lateinit var reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence

    @MockK
    private lateinit var typologyPersistence: ProgrammeTypologyErrorsPersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureVerification: UpdateProjectPartnerControlReportExpenditureVerification

    @BeforeEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - without parking`() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55L) } returns
            listOf(verification.copy())
        every { typologyPersistence.getAllTypologyErrors() } returns listOf(existingError)

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate))
        } returns listOf(verification.copy())

        every { reportParkedExpenditurePersistence.parkExpenditures(emptyList()) } answers { }
        every { reportParkedExpenditurePersistence.unParkExpenditures(emptyList()) } answers { }

        val partnerReport = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(partnerId = 17L , reportId = 55L) } returns partnerReport
        every { partnerReport.status } returns ReportStatus.InControl
        every { partnerReport.lastControlReopening } returns null

        assertThat(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55L, listOf(expenditureUpdateValidWithoutParking))
        ).isEqualTo(listOf(verification.copy()))

        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithoutParking)
        assertTrue(slotToUpdate.captured.first().partOfSample)
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - with deduction`() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55L) } returns
                listOf(verification.copy(declaredAmountAfterSubmission = BigDecimal.valueOf(700)))
        every { typologyPersistence.getAllTypologyErrors() } returns listOf(existingError)

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate))
        } returns listOf(verification.copy())

        every { reportParkedExpenditurePersistence.parkExpenditures(emptyList()) } answers { }
        every { reportParkedExpenditurePersistence.unParkExpenditures(emptyList()) } answers { }
        val partnerReport = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(partnerId = 17L , reportId = 55L) } returns partnerReport
        every { partnerReport.status } returns ReportStatus.InControl
        every { partnerReport.lastControlReopening } returns null

        assertThat(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55L, listOf(expenditureUpdateValidWithDeduction))
        ).isEqualTo(listOf(verification.copy()))

        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithDeduction)
        assertThat(slotToUpdate.captured.first().deductedAmount.equals(200L))
        assertTrue(slotToUpdate.captured.first().partOfSample)
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

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate))
        } returns listOf(verificationParked)

        val parkedItems = slot<Collection<ParkExpenditureData>>()
        every { reportParkedExpenditurePersistence.parkExpenditures(capture(parkedItems)) } answers { }
        val unParkedIds = slot<Collection<Long>>()
        every { reportParkedExpenditurePersistence.unParkExpenditures(capture(unParkedIds)) } answers { }

        assertThat(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55L, listOf(expenditureUpdateValidWithParking))
        ).containsExactly(verificationParked)

        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithParking)
        assertTrue(slotToUpdate.captured.first().partOfSample)

        val parkedOn = parkedItems.captured.toList().get(0).parkedOn
        assertThat(parkedItems.captured).containsExactly(
            ParkExpenditureData(expenditureId=14L, originalReportId=14L, originalProjectReportId = null, originalNumber=9, parkedOn = parkedOn)
        )
        assertThat(unParkedIds.captured).isEmpty()
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - with un-parking`() {
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 19L, reportId = 54L) } returns
            listOf(verification.copy(
                parked = true,
                parkingMetadata = ExpenditureParkingMetadata(
                    reportOfOriginId = 54L,
                    reportOfOriginNumber = 4,
                    reportProjectOfOriginId = null,
                    originalExpenditureNumber = 1
                ),
            ))
        every { typologyPersistence.getAllTypologyErrors() } returns emptyList()
        every { reportPersistence.getPartnerReportById(19L, 54L) } returns projectPartnerReport.copy(id = 54L)
        every { partnerPersistence.getProjectIdForPartnerId(19L, "v1.0") } returns 40L
        every { projectPartnerReport.identification.projectIdentifier } returns "identifier"
        every { projectPartnerReport.identification.projectAcronym } returns "acronym"
        every { projectPartnerReport.identification.partnerRole } returns ProjectPartnerRole.PARTNER

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence
            .updatePartnerControlReportExpenditureVerification(partnerId = 19L, reportId = 54, capture(slotToUpdate))
        } returns listOf(verificationUnParked)

        val parkedItems = slot<Collection<ParkExpenditureData>>()
        every { reportParkedExpenditurePersistence.parkExpenditures(capture(parkedItems)) } answers { }
        val unParkedIds = slot<Collection<Long>>()
        every { reportParkedExpenditurePersistence.unParkExpenditures(capture(unParkedIds)) } answers { }

        assertThat(updatePartnerReportExpenditureVerification.updatePartnerReportExpenditureVerification(
            partnerId = 19L,
            reportId = 54L,
            listOf(expenditureUpdateValidWithParking.copy(parked = false, deductedAmount = BigDecimal.ZERO)),
        )).containsExactly(verificationUnParked)
        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithParking.copy(parked = false, certifiedAmount = BigDecimal.ONE))
        assertTrue(slotToUpdate.captured.first().partOfSample)

        assertThat(parkedItems.captured).isEmpty()
        assertThat(unParkedIds.captured).containsExactly(14L)
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - topology error`() {
        val partnerReport = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(partnerId = 11L , reportId = 4L) } returns partnerReport
        every { partnerReport.status } returns ReportStatus.InControl
        every { partnerReport.lastControlReopening } returns null

        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 11L, reportId = 4L) } returns
            listOf(verification.copy(id = 1L))
        every { typologyPersistence.getAllTypologyErrors() } returns emptyList()
        every { reportParkedExpenditurePersistence.parkExpenditures(emptyList()) } answers { }
        every { reportParkedExpenditurePersistence.unParkExpenditures(emptyList()) } answers { }

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence.updatePartnerControlReportExpenditureVerification(partnerId = 11L, reportId = 4L, capture(slotToUpdate)) } returns
                emptyList()

        assertThrows<TypologyOfErrorMissing> {
            updatePartnerReportExpenditureVerification
                .updatePartnerReportExpenditureVerification(partnerId = 11L, reportId = 4L, listOf(expenditureUpdateInvalid))
        }
    }

    @Test
    fun `updatePartnerReportExpenditureVerification - with sampling locks`() {
        val partnerReport = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(partnerId = 17L , reportId = 55L) } returns partnerReport
        every { partnerReport.status } returns ReportStatus.InControl
        every { partnerReport.lastControlReopening } returns null

        val verificationToBeUpdated = verification.copy(partOfSampleLocked = true, partOfSample = true, typologyOfErrorId = existingError.id, parked = false)
        every { reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55L) } returns
            listOf(verificationToBeUpdated)
        every { typologyPersistence.getAllTypologyErrors() } returns listOf(existingError)
        every { reportParkedExpenditurePersistence.parkExpenditures(emptyList()) } answers { }
        every { reportParkedExpenditurePersistence.unParkExpenditures(emptyList()) } answers { }

        val slotToUpdate = slot<List<ExpenditureVerificationUpdate>>()
        every { reportExpenditurePersistence.updatePartnerControlReportExpenditureVerification(partnerId = 17L, reportId = 55, capture(slotToUpdate)) } returns
                listOf(verificationToBeUpdated)

        assertTrue(updatePartnerReportExpenditureVerification
            .updatePartnerReportExpenditureVerification(partnerId = 17L, reportId = 55, listOf(expenditureUpdateWithoutSampled))
            .first().partOfSample)

        assertThat(slotToUpdate.captured).containsExactly(expectedUpdateWithoutSampled)
    }
}
