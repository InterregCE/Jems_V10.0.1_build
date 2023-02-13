package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.authorization.ProjectPartnerReportAuthorization
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.listFiles.ListReportControlFiles
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListReportControlCertificatesTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 1L
        private const val REPORT_ID = 2L
    }

    @MockK
    lateinit var projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence

    @MockK
    lateinit var partnerReportAuth: ProjectPartnerReportAuthorization

    @InjectMockKs
    lateinit var interactor: ListReportControlFiles

    @BeforeEach
    fun setup() {
        clearMocks(projectPartnerReportControlFilePersistence)
    }

    @Test
    fun `list report control certificates`() {
        val result = mockk<Page<PartnerReportControlFile>>()
        every { partnerReportAuth.canViewPartnerControlReport(PARTNER_ID, REPORT_ID)} returns true
        every { projectPartnerReportControlFilePersistence.listReportControlFiles(REPORT_ID, Pageable.unpaged()) } returns result

        assertThat(interactor.list(PARTNER_ID, REPORT_ID, Pageable.unpaged())).isEqualTo(result)
    }
}
