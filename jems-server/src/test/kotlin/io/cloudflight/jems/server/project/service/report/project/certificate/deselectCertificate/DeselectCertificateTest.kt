package io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class DeselectCertificateTest : UnitTest() {

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @InjectMockKs
    lateinit var interactor: DeselectCertificate

    @BeforeEach
    fun reset() {
        clearMocks(projectReportPersistence, projectReportCertificatePersistence)
    }

    @ParameterizedTest(name = "deselectCertificate (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "VerificationReOpenedLast"])
    fun deselectCertificate(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.id } returns 498L
        every { report.status } returns status
        every { projectReportPersistence.getReportById(projectId = 1L, reportId = 498L) } returns report

        every { projectReportCertificatePersistence.deselectCertificate(projectReportId = 498L, certificateId = 22L) } answers { }

        interactor.deselectCertificate(projectId = 1L, reportId = 498L, certificateId = 22L)
        verify(exactly = 1) { projectReportCertificatePersistence.deselectCertificate(projectReportId = 498L, certificateId = 22L) }
    }

    @ParameterizedTest(name = "deselectCertificate - wrong status {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "VerificationReOpenedLast"], mode = EnumSource.Mode.EXCLUDE)
    fun `deselectCertificate - wrong status`(status: ProjectReportStatus) {
        val reportId = 500L + status.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { projectReportPersistence.getReportById(projectId = 1L, reportId) } returns report

        every { projectReportCertificatePersistence.deselectCertificate(reportId, certificateId = 22L) } answers { }

        assertThrows<CertificatesCannotBeChangedWhenReOpenModeIsLimited> {
            interactor.deselectCertificate(projectId = 1L, reportId = reportId, certificateId = 22L)
        }
        verify(exactly = 0) { projectReportCertificatePersistence.deselectCertificate(projectReportId = reportId, certificateId = 22L) }
    }

}
