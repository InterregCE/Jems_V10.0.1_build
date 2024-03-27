package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

internal class UpdateProjectReportTest : UnitTest() {

    companion object {
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val NOW = ZonedDateTime.now()
        private val WEEK_AGO = ZonedDateTime.now().minusWeeks(1)
        private val NEXT_MONTH = ZonedDateTime.now().plusMonths(1)

        private fun deadline(id: Long, type: ContractingDeadlineType = ContractingDeadlineType.Finance): ProjectContractingReportingSchedule {
            val deadline = mockk<ProjectContractingReportingSchedule>()
            every { deadline.id } returns id
            every { deadline.type } returns type
            return deadline
        }

        private fun report(version: String, status: ProjectReportStatus, type: ContractingDeadlineType, deadlineId: Long? = null): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.linkedFormVersion } returns version
            every { report.deadlineId } returns deadlineId
            every { report.type } returns type
            every { report.status } returns status
            every { report.id } returns 222L
            return report
        }

        private val mockedResult = ProjectReportModel(
            id = 222L,
            reportNumber = 8,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "version",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            deadlineId = 96L,
            type = ContractingDeadlineType.Content,
            periodNumber = 12,
            reportingDate = TOMORROW,
            finalReport = false,

            projectId = 56L,
            projectIdentifier = "identif",
            projectAcronym = "acron",
            leadPartnerNameInOriginalLanguage = "orig",
            leadPartnerNameInEnglish = "en",
            spfPartnerId = null,
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            lastReSubmission = mockk(),
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH,
            amountRequested = BigDecimal.ZERO,
            totalEligibleAfterVerification = BigDecimal.ZERO,
            lastVerificationReOpening = null,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "Description"
        )

        private val expectedResult = ProjectReport(
            id = 222L,
            reportNumber = 8,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "version",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            deadlineId = 96L,
            type = ContractingDeadlineType.Content,
            periodDetail = ProjectPeriod(12, 7, 15),
            reportingDate = TOMORROW,
            finalReport = false,

            projectId = 56L,
            projectIdentifier = "identif",
            projectAcronym = "acron",
            leadPartnerNameInOriginalLanguage = "orig",
            leadPartnerNameInEnglish = "en",
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH,
            verificationLastReOpenDate = null
        )
    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var projectPersistence: ProjectPersistence
    @MockK
    private lateinit var deadlinePersistence: ContractingReportingPersistence
    @MockK
    private lateinit var certificatePersistence: ProjectReportCertificatePersistence
    @MockK
    private lateinit var paymentPersistence: PaymentPersistence
    @MockK
    private lateinit var paymentApplicationToEcLinkPersistence: PaymentApplicationToEcLinkPersistence


    @InjectMockKs
    lateinit var interactor: UpdateProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, projectPersistence, deadlinePersistence,
            certificatePersistence, paymentPersistence, paymentApplicationToEcLinkPersistence)
    }

    @ParameterizedTest(name = "update with deadline link - {0}")
    @EnumSource(value = ProjectReportStatus::class)
    fun `update with deadline link`(status: ProjectReportStatus) {
        val projectId = 50L + status.ordinal
        every { reportPersistence.getReportById(projectId, reportId = 87L) } returns
                report("version", status, ContractingDeadlineType.Finance, deadlineId = 7L)
        every { projectPersistence.getProjectPeriods(projectId, "version") } returns
            listOf(ProjectPeriod(mockedResult.periodNumber!!, 7, 15))
        every { deadlinePersistence.getContractingReportingDeadline(projectId, deadlineId = 7L) } returns deadline(7L)
        every { paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(222L) } returns setOf()
        every { paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(222L) } returns setOf()

        val slotStartDate = slot<LocalDate>()
        val slotEndDate = slot<LocalDate>()
        val slotDeadline = slot<ProjectReportDeadline>()
        every { reportPersistence.updateReport(projectId, reportId = 87L,
            startDate = capture(slotStartDate), endDate = capture(slotEndDate), capture(slotDeadline))
        } returns mockedResult

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = 7L,
            type = null,
            periodNumber = null,
            reportingDate = null,
            finalReport = null,
        )
        // this assertion is only testing mapper on result
        assertThat(interactor.updateReport(projectId, reportId = 87L, data)).isEqualTo(expectedResult)

        // this is testing input data for update itself
        assertThat(slotStartDate.captured).isEqualTo(MONTH_AGO)
        assertThat(slotEndDate.captured).isEqualTo(TOMORROW)
        assertThat(slotDeadline.captured).isEqualTo(ProjectReportDeadline(
            deadlineId = 7L, type = null, periodNumber = null, reportingDate = null, finalReport = null,
        ))
    }

    @ParameterizedTest(name = "update manually without deadline link {0}")
    @EnumSource(value = ProjectReportStatus::class)
    fun `update manually without deadline link`(status: ProjectReportStatus) {
        val projectId = 60L + status.ordinal
        every { reportPersistence.getReportById(projectId, reportId = 82L) } returns report("5.2a", status, ContractingDeadlineType.Content)
        every { projectPersistence.getProjectPeriods(projectId, "5.2a") } returns
            listOf(ProjectPeriod(mockedResult.periodNumber!!, 7, 15))
        every { paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(222L) } returns setOf()
        every { paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(222L) } returns setOf()

        val slotStartDate = slot<LocalDate>()
        val slotEndDate = slot<LocalDate>()
        val slotDeadline = slot<ProjectReportDeadline>()
        every { reportPersistence.updateReport(projectId, reportId = 82L,
            startDate = capture(slotStartDate), endDate = capture(slotEndDate), capture(slotDeadline))
        } returns mockedResult

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = 12,
            reportingDate = YESTERDAY,
            finalReport = false,
        )
        // this assertion is only testing mapper on result
        assertThat(interactor.updateReport(projectId, reportId = 82L, data)).isEqualTo(expectedResult)

        // this is testing input data for update itself
        assertThat(slotStartDate.captured).isEqualTo(MONTH_AGO)
        assertThat(slotEndDate.captured).isEqualTo(TOMORROW)
        assertThat(slotDeadline.captured).isEqualTo(ProjectReportDeadline(
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = 12,
            reportingDate = YESTERDAY,
            finalReport = false,
        ))
    }

    @ParameterizedTest(name = "update type and deadline when reopened {0}")
    @CsvSource(value = [
        "Submitted,Finance,Finance,15,26",
        "Submitted,Finance,Content,16,16",
        "Submitted,Content,Finance,17,28",
        "ReOpenSubmittedLast,Finance,Finance,18,18",
        "ReOpenSubmittedLimited,Finance,Finance,19,19",
        "InVerification,Finance,Finance,20,20",
        "VerificationReOpenedLast,Finance,Finance,21,21",
        "VerificationReOpenedLimited,Finance,Finance,22,22",
    ])
    fun `update type and deadline when reopened`(
        status: ProjectReportStatus,
        oldType: ContractingDeadlineType,
        newType: ContractingDeadlineType,
        oldDeadlineId: Long,
        newDeadlineId: Long,
    ) {
        val projectId = 70L
        every { reportPersistence.getReportById(projectId, reportId = 82L) } returns report("5.2a", status, oldType, oldDeadlineId)
        every { projectPersistence.getProjectPeriods(projectId, "5.2a") } returns
            listOf(ProjectPeriod(mockedResult.periodNumber!!, 7, 15))

        if (newDeadlineId > 0L)
            every { deadlinePersistence.getContractingReportingDeadline(projectId, deadlineId = newDeadlineId) } returns
                    deadline(newDeadlineId, newType)

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = newType,
            periodNumber = 12,
            reportingDate = YESTERDAY,
            finalReport = false,
        )
        assertThrows<TypeChangeIsForbiddenWhenReportIsReOpened> { interactor.updateReport(projectId, reportId = 82L, data) }
    }

    @ParameterizedTest(name = "update type and deadline when type changed {0} -> {1}")
    @CsvSource(value = ["Finance,Content", "Both,Content"])
    fun `update type and deadline when type changed`(
        oldType: ContractingDeadlineType,
        newType: ContractingDeadlineType,
    ) {
        val projectId = 70L
        every { reportPersistence.getReportById(projectId, reportId = 82L) } returns report("5.2a", ProjectReportStatus.Draft, oldType)
        every { projectPersistence.getProjectPeriods(projectId, "5.2a") } returns
            listOf(ProjectPeriod(mockedResult.periodNumber!!, 7, 15))

        every { certificatePersistence.deselectCertificatesOfProjectReport(82L) } answers { }
        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = newType,
            periodNumber = 12,
            reportingDate = YESTERDAY,
            finalReport = false,
        )
        every { reportPersistence.updateReport(projectId, reportId = 82L, any(), any(), any()) } returns mockedResult
        interactor.updateReport(projectId, 82L, data)
        verify(exactly = 1) { reportPersistence.updateReport(projectId, 82L, any(), any(), any()) }
        verify(exactly = 1) { certificatePersistence.deselectCertificatesOfProjectReport(82L) }
    }

    @Test
    fun `update manually - wrong period number`() {
        val projectId = 54L
        every { reportPersistence.getReportById(projectId, reportId = 84L) } returns report("5.4a", mockk(), mockk())
        every { projectPersistence.getProjectPeriods(projectId, "5.4a") } returns emptyList()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = -1,
            reportingDate = YESTERDAY,
            finalReport = false,
        )
        assertThrows<PeriodNumberInvalid> { interactor.updateReport(projectId, reportId = 84L, data) }
    }

    @Test
    fun `update manually - missing mandatory fields`() {
        val projectId = 56L
        every { reportPersistence.getReportById(projectId, reportId = 86L) } returns report("5.6a", mockk(), mockk())
        every { projectPersistence.getProjectPeriods(projectId, "5.6a") } returns emptyList()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = 58,
            reportingDate = YESTERDAY,
            finalReport = false,
        )
        assertThrows<LinkToDeadlineNotProvidedAndDataMissing> {
            interactor.updateReport(projectId, reportId = 86L, data.copy(type = null))
        }
        assertThrows<LinkToDeadlineNotProvidedAndDataMissing> {
            interactor.updateReport(projectId, reportId = 86L, data.copy(periodNumber = null))
        }
        assertThrows<LinkToDeadlineNotProvidedAndDataMissing> {
            interactor.updateReport(projectId, reportId = 86L, data.copy(reportingDate = null))
        }
    }

    @Test
    fun `update with deadline - forbidden data provided`() {
        val projectId = 58L
        every { reportPersistence.getReportById(projectId, reportId = 88L) } returns report("version8", mockk(), mockk())
        every { projectPersistence.getProjectPeriods(projectId, "version8") } returns emptyList()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = 7L,
            type = null,
            periodNumber = null,
            reportingDate = null,
            finalReport = false,
        )
        assertThrows<LinkToDeadlineProvidedWithManualDataOverride> {
            interactor.updateReport(projectId, reportId = 88L, data.copy(type = ContractingDeadlineType.Content))
        }
        assertThrows<LinkToDeadlineProvidedWithManualDataOverride> {
            interactor.updateReport(projectId, reportId = 88L, data.copy(periodNumber = 96))
        }
        assertThrows<LinkToDeadlineProvidedWithManualDataOverride> {
            interactor.updateReport(projectId, reportId = 88L, data.copy(reportingDate = TOMORROW))
        }
    }

}
