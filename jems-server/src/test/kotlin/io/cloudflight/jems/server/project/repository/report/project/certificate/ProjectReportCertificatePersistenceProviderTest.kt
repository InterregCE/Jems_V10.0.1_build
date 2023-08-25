package io.cloudflight.jems.server.project.repository.report.project.certificate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.partner.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.model.CertificateSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportIdentificationSummary
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class ProjectReportCertificatePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 17L

        private fun partnerReport(
            projectReport: ProjectReportEntity?,
            identification: PartnerReportIdentificationEntity = mockk(),
            createdAt: ZonedDateTime = ZonedDateTime.now()
        ): ProjectPartnerReportEntity {
            return ProjectPartnerReportEntity(
                id = 499,
                partnerId = 72,
                number = 4,
                status = ReportStatus.Certified,
                applicationFormVersion = "v",
                firstSubmission = null,
                lastReSubmission = null,
                controlEnd = null,
                identification = identification,
                createdAt = createdAt,
                projectReport = projectReport,
                lastControlReopening = null
            )
        }

        private val partnerReportIdentificationSummary = ProjectPartnerReportIdentificationSummary(
            id = 1L,
            reportNumber = 10,
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = 99L,
            sumTotalEligibleAfterControl = BigDecimal(800),
            nextReportForecast = BigDecimal(75),
            periodDetail = null
        )

        private val reportIdentificationSummary = ReportIdentificationSummary(
            partnerReportId = 1L,
            partnerReportNumber = 10,
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = 99L,
            totalEligibleAfterControl = BigDecimal(800),
            nextReportForecast = BigDecimal(75),
            periodNumber = null,
            endDate = LocalDate.now(),
            startDate = LocalDate.now().minusDays(1),
            periodBudget = null,
            periodBudgetCumulative = null,
            periodEnd = null,
            periodStart = null
        )

    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository
    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportCertificatePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(projectReportRepository, partnerReportRepository)
    }

    @Test
    fun `deselectCertificate - with correct project report`() {
        val projectReport = mockk<ProjectReportEntity>()
        every { projectReport.id } returns 978L

        val certificate = partnerReport(projectReport)
        every { partnerReportRepository.findById(75L) } returns Optional.of(certificate)

        persistence.deselectCertificate(projectReportId = 978L, certificateId = 75L)

        assertThat(certificate.projectReport).isNull()
    }

    @Test
    fun `deselectCertificate - with invalid project report`() {
        val projectReport = mockk<ProjectReportEntity>()
        every { projectReport.id } returns 911L

        val certificate = partnerReport(projectReport)
        every { partnerReportRepository.findById(76L) } returns Optional.of(certificate)

        persistence.deselectCertificate(projectReportId = 9999L, certificateId = 76L)

        assertThat(certificate.projectReport).isEqualTo(projectReport)
    }

    @Test
    fun deselectCertificatesOfProjectReport() {
        val reports = listOf(
            mockk<ProjectReportEntity>(),
            mockk<ProjectReportEntity>(),
        )

        val certificates = reports.map { partnerReport(it) }
        every { partnerReportRepository.findAllByProjectReportId(921L) } returns certificates

        persistence.deselectCertificatesOfProjectReport(921L)

        assertThat(certificates.map { it.projectReport }).allMatch { it == null }
    }

    @Test
    fun selectCertificate() {
        val certificate = partnerReport(null)
        every { partnerReportRepository.getById(77L) } returns certificate

        val projectReport = mockk<ProjectReportEntity>()
        every { projectReportRepository.getById(100L) } returns projectReport

        persistence.selectCertificate(projectReportId = 100L, certificateId = 77L)

        assertThat(certificate.projectReport).isEqualTo(projectReport)
    }

    @Test
    fun `selectCertificate - not available`() {
        val projectReport = mockk<ProjectReportEntity>()
        val certificate = partnerReport(projectReport)
        every { partnerReportRepository.getById(78L) } returns certificate

        persistence.selectCertificate(projectReportId = 110L, certificateId = 78L)

        assertThat(certificate.projectReport).isEqualTo(projectReport)
        verify(exactly = 0) { projectReportRepository.getById(any()) }
    }

    @Test
    fun listCertificates() {
        val now = Instant.now()
        val certificate = mockk<CertificateSummary>()
        every { certificate.partnerReportId } returns 845L
        every { certificate.partnerReportNumber } returns 22
        every { certificate.partnerId } returns 252L
        every { certificate.partnerRole } returns ProjectPartnerRole.LEAD_PARTNER
        every { certificate.partnerNumber } returns 250
        every { certificate.totalEligibleAfterControl } returns BigDecimal.valueOf(7984L, 2)
        every { certificate.controlEnd } returns now
        every { certificate.projectReportId } returns 887L
        every { certificate.projectReportNumber } returns 15

        every { partnerReportRepository.findAllCertificates(partnerIds = setOf(PARTNER_ID), Pageable.unpaged()) } returns PageImpl(listOf(certificate))

        assertThat(persistence.listCertificates(setOf(PARTNER_ID), Pageable.unpaged())).containsExactly(
            PartnerReportCertificate(
                partnerReportId = 845L,
                partnerReportNumber = 22,
                partnerId = 252L,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerNumber = 250,
                totalEligibleAfterControl = BigDecimal.valueOf(7984L, 2),
                controlEnd = now.atZone(ZoneId.systemDefault()),
                projectReportId = 887L,
                projectReportNumber = 15,
            ),
        )
    }

    @Test
    fun listCertificatesOfProjectReport() {
        val identification = PartnerReportIdentificationEntity(
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            partnerNumber = 4,
            partnerAbbreviation = "P-4",
            partnerRole = ProjectPartnerRole.PARTNER,
            nameInOriginalLanguage = null,
            nameInEnglish = null,
            legalStatus = null,
            country = null,
            currency = null,
        )
        val time = ZonedDateTime.now()
        val certificate = partnerReport(mockk(), identification, time)

        every { partnerReportRepository.findAllByProjectReportId(47L) } returns listOf(certificate)
        assertThat(persistence.listCertificatesOfProjectReport(47L)).containsExactly(
            ProjectPartnerReportSubmissionSummary(
                id = 499L,
                reportNumber = 4,
                status = ReportStatus.Certified,
                version = "v",
                firstSubmission = null,
                controlEnd = null,
                createdAt = time,
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerAbbreviation = "P-4",
                partnerNumber = 4,
                partnerRole = ProjectPartnerRole.PARTNER,
                partnerId = 72L
            )
        )
    }

    @Test
    fun getIdentificationSummariesOfProjectReport() {
        every { partnerReportRepository.findAllIdentificationSummariesByProjectReportId(10L) } returns
            listOf(reportIdentificationSummary)
        assertThat(persistence.getIdentificationSummariesOfProjectReport(10L)).containsExactly(
            partnerReportIdentificationSummary
        )
    }

}
