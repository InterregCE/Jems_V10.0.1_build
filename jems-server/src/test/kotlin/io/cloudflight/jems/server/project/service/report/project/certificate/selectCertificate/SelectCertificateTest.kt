package io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
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

internal class SelectCertificateTest : UnitTest() {

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    private lateinit var partnerReportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    private lateinit var interactor: SelectCertificate

    @BeforeEach
    fun reset() {
        clearMocks(projectReportPersistence, projectReportCertificatePersistence, partnerReportPersistence)
    }

    @ParameterizedTest(name = "selectCertificate {0}")
    @EnumSource(value = ContractingDeadlineType::class, names = ["Finance", "Both"])
    fun selectCertificate(type: ContractingDeadlineType) {
        val reportId = 497L + type.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.id } returns reportId
        every { report.type } returns type
        every { projectReportPersistence.getReportById(projectId = 1L, reportId) } returns report

        val certificateId = 25L
        val certificate = mockk<ProjectPartnerReportStatusAndVersion>()
        every { certificate.reportId } returns certificateId
        every { certificate.status } returns ReportStatus.Certified
        every { partnerReportPersistence.getPartnerReportByProjectIdAndId(1L, certificateId) } returns certificate

        every { projectReportCertificatePersistence.selectCertificate(projectReportId = reportId, certificateId) } answers { }

        interactor.selectCertificate(projectId = 1L, reportId = reportId, certificateId = certificateId)
        verify(exactly = 1) { projectReportCertificatePersistence.selectCertificate(projectReportId = reportId, certificateId) }
    }

    @ParameterizedTest(name = "selectCertificate - not finalized {0}")
    @EnumSource(value = ReportStatus::class, names = ["Certified"], mode = EnumSource.Mode.EXCLUDE)
    fun `selectCertificate - not finalized`(status: ReportStatus) {
        val reportId = 490L
        val report = mockk<ProjectReportModel>()
        every { report.id } returns reportId
        every { report.type } returns ContractingDeadlineType.Both
        every { projectReportPersistence.getReportById(projectId = 1L, reportId) } returns report

        val certificateId = 28L + status.ordinal
        val certificate = mockk<ProjectPartnerReportStatusAndVersion>()
        every { certificate.reportId } returns certificateId
        every { certificate.status } returns status
        every { partnerReportPersistence.getPartnerReportByProjectIdAndId(1L, certificateId) } returns certificate

        assertThrows<CertificateIsNotFinalized> { interactor.selectCertificate(projectId = 1L, reportId = reportId, certificateId = certificateId) }
        verify(exactly = 0) { projectReportCertificatePersistence.selectCertificate(any(), any()) }
    }

    @ParameterizedTest(name = "selectCertificate - project report is not finance {0}")
    @EnumSource(value = ContractingDeadlineType::class, names = ["Finance", "Both"], mode = EnumSource.Mode.EXCLUDE)
    fun `selectCertificate - project report is not finance`(type: ContractingDeadlineType) {
        val reportId = 390L + type.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.id } returns reportId
        every { report.type } returns type
        every { projectReportPersistence.getReportById(projectId = 1L, reportId) } returns report

        assertThrows<ProjectReportDoesNotIncludeFinance> {
            interactor.selectCertificate(projectId = 1L, reportId = reportId, 0L)
        }
        verify(exactly = 0) { projectReportCertificatePersistence.selectCertificate(any(), any()) }
    }

    @ParameterizedTest(name = "selectCertificate - certificate not found {0}")
    @EnumSource(value = ContractingDeadlineType::class, names = ["Finance", "Both"])
    fun `selectCertificate - certificate not found`(type: ContractingDeadlineType) {
        val reportId = 380L + type.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.id } returns reportId
        every { report.type } returns type
        every { projectReportPersistence.getReportById(projectId = 1L, reportId) } returns report

        every { partnerReportPersistence.getPartnerReportByProjectIdAndId(1L, 222L) } returns null
        assertThrows<CertificateNotFound> {
            interactor.selectCertificate(projectId = 1L, reportId = reportId, 222L)
        }
        verify(exactly = 0) { projectReportCertificatePersistence.selectCertificate(any(), any()) }
    }

}
