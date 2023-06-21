package io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate

import io.cloudflight.jems.server.UnitTest
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
import org.junit.jupiter.api.Test

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

    @Test
    fun deselectCertificate() {
        val report = mockk<ProjectReportModel>()
        every { report.id } returns 498L
        every { projectReportPersistence.getReportById(projectId = 1L, reportId = 498L) } returns report

        every { projectReportCertificatePersistence.deselectCertificate(projectReportId = 498L, certificateId = 22L) } answers { }

        interactor.deselectCertificate(projectId = 1L, reportId = 498L, certificateId = 22L)
        verify(exactly = 1) { projectReportCertificatePersistence.deselectCertificate(projectReportId = 498L, certificateId = 22L) }
    }

}
