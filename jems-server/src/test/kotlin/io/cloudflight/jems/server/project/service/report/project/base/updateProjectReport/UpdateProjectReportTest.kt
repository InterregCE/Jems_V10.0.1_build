package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
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
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateProjectReportTest : UnitTest() {

    companion object {
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val NOW = ZonedDateTime.now()
        private val WEEK_AGO = ZonedDateTime.now().minusWeeks(1)
        private val NEXT_MONTH = ZonedDateTime.now().plusMonths(1)

        private fun deadline(id: Long): ProjectContractingReportingSchedule {
            val deadline = mockk<ProjectContractingReportingSchedule>()
            every { deadline.id } returns id
            every { deadline.type } returns ContractingDeadlineType.Finance
            return deadline
        }

        private fun report(version: String): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.linkedFormVersion } returns version
            every { report.deadlineId } returns null
            every { report.type } returns ContractingDeadlineType.Both
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

            projectId = 56L,
            projectIdentifier = "identif",
            projectAcronym = "acron",
            leadPartnerNameInOriginalLanguage = "orig",
            leadPartnerNameInEnglish = "en",
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH,
            amountRequested = BigDecimal.ZERO,
            totalEligibleAfterVerification = BigDecimal.ZERO,
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

            projectId = 56L,
            projectIdentifier = "identif",
            projectAcronym = "acron",
            leadPartnerNameInOriginalLanguage = "orig",
            leadPartnerNameInEnglish = "en",
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH
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

    @InjectMockKs
    lateinit var interactor: UpdateProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, projectPersistence, deadlinePersistence)
    }

    @Test
    fun `update with deadline link`() {
        val projectId = 50L
        every { reportPersistence.getReportById(projectId, reportId = 87L) } returns report("version")
        every { projectPersistence.getProjectPeriods(projectId, "version") } returns
            listOf(ProjectPeriod(mockedResult.periodNumber!!, 7, 15))
        every { deadlinePersistence.getContractingReportingDeadline(projectId, deadlineId = 7L) } returns deadline(7L)

        val slotStartDate = slot<LocalDate>()
        val slotEndDate = slot<LocalDate>()
        val slotDeadline = slot<ProjectReportDeadline>()
        every { reportPersistence.updateReport(projectId, reportId = 87L,
            startDate = capture(slotStartDate), endDate = capture(slotEndDate), capture(slotDeadline))
        } returns mockedResult
        every { certificatePersistence.listCertificatesOfProjectReport(87L) } returns listOf()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = 7L,
            type = null,
            periodNumber = null,
            reportingDate = null,
        )
        // this assertion is only testing mapper on result
        assertThat(interactor.updateReport(projectId, reportId = 87L, data)).isEqualTo(expectedResult)

        // this is testing input data for update itself
        assertThat(slotStartDate.captured).isEqualTo(MONTH_AGO)
        assertThat(slotEndDate.captured).isEqualTo(TOMORROW)
        assertThat(slotDeadline.captured).isEqualTo(ProjectReportDeadline(
            deadlineId = 7L, type = null, periodNumber = null, reportingDate = null
        ))
    }

    @Test
    fun `update manually without deadline link`() {
        val projectId = 52L
        every { reportPersistence.getReportById(projectId, reportId = 82L) } returns report("5.2a")
        every { projectPersistence.getProjectPeriods(projectId, "5.2a") } returns
            listOf(ProjectPeriod(mockedResult.periodNumber!!, 7, 15))

        val slotStartDate = slot<LocalDate>()
        val slotEndDate = slot<LocalDate>()
        val slotDeadline = slot<ProjectReportDeadline>()
        every { reportPersistence.updateReport(projectId, reportId = 82L,
            startDate = capture(slotStartDate), endDate = capture(slotEndDate), capture(slotDeadline))
        } returns mockedResult
        every { certificatePersistence.listCertificatesOfProjectReport(82L) } returns listOf(
            ProjectPartnerReportSubmissionSummary(
                id = 499L,
                reportNumber = 4,
                status = ReportStatus.Certified,
                version = "v",
                firstSubmission = null,
                controlEnd = null,
                createdAt = ZonedDateTime.now(),
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerAbbreviation = "P-4",
                partnerNumber = 4,
                partnerRole = ProjectPartnerRole.PARTNER,
                partnerId = 72L
            )
        )
        every { certificatePersistence.deselectCertificate(82L, 499L) } returnsArgument 0

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = 12,
            reportingDate = YESTERDAY,
        )
        // this assertion is only testing mapper on result
        assertThat(interactor.updateReport(projectId, reportId = 82L, data)).isEqualTo(expectedResult)

        // this is testing input data for update itself
        assertThat(slotStartDate.captured).isEqualTo(MONTH_AGO)
        assertThat(slotEndDate.captured).isEqualTo(TOMORROW)
        assertThat(slotDeadline.captured).isEqualTo(ProjectReportDeadline(
            deadlineId = null, type = ContractingDeadlineType.Content, periodNumber = 12, reportingDate = YESTERDAY
        ))
    }

    @Test
    fun `update manually - wrong period number`() {
        val projectId = 54L
        every { reportPersistence.getReportById(projectId, reportId = 84L) } returns report("5.4a")
        every { projectPersistence.getProjectPeriods(projectId, "5.4a") } returns emptyList()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = -1,
            reportingDate = YESTERDAY,
        )
        assertThrows<PeriodNumberInvalid> { interactor.updateReport(projectId, reportId = 84L, data) }
    }

    @Test
    fun `update manually - missing mandatory fields`() {
        val projectId = 56L
        every { reportPersistence.getReportById(projectId, reportId = 86L) } returns report("5.6a")
        every { projectPersistence.getProjectPeriods(projectId, "5.6a") } returns emptyList()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = 58,
            reportingDate = YESTERDAY,
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
        every { reportPersistence.getReportById(projectId, reportId = 88L) } returns report("version8")
        every { projectPersistence.getProjectPeriods(projectId, "version8") } returns emptyList()

        val data = ProjectReportUpdate(
            startDate = MONTH_AGO,
            endDate = TOMORROW,
            deadlineId = 7L,
            type = null,
            periodNumber = null,
            reportingDate = null,
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
