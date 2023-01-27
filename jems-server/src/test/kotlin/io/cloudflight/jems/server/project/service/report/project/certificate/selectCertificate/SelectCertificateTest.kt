package io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate

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

internal class SelectCertificateTest : UnitTest() {

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @InjectMockKs
    lateinit var interactor: SelectCertificate

    @BeforeEach
    fun reset() {
        clearMocks(projectReportPersistence, projectReportCertificatePersistence)
    }

    @Test
    fun selectCertificate() {
        val report = mockk<ProjectReportModel>()
        every { report.id } returns 497L
        every { projectReportPersistence.getReportById(projectId = 1L, reportId = 497L) } returns report

        every { projectReportCertificatePersistence.selectCertificate(projectReportId = 497L, certificateId = 22L) } answers { }

        interactor.selectCertificate(projectId = 1L, reportId = 497L, certificateId = 22L)
        verify(exactly = 1) { projectReportCertificatePersistence.selectCertificate(projectReportId = 497L, certificateId = 22L) }
    }

}
