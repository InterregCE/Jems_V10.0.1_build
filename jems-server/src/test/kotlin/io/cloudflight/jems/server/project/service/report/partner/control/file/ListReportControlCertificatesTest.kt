package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.ProjectPartnerReportAuthorization
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates.ListReportControlCertificates
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
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
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerReportAuth: ProjectPartnerReportAuthorization

    @InjectMockKs
    lateinit var interactor: ListReportControlCertificates

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test
    fun `list report control certificates`() {
        val filter = setOf(JemsFileType.ControlCertificate)
        val indexPrefix = slot<String>()
        val result = mockk<Page<JemsFile>>()
        every { partnerReportAuth.canViewPartnerControlReport(PARTNER_ID, REPORT_ID)} returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns 5L // projectId
        every { filePersistence.listAttachments(Pageable.unpaged(), capture(indexPrefix), filter, any()) } returns result

        assertThat(interactor.list(PARTNER_ID, REPORT_ID, Pageable.unpaged())).isEqualTo(result)
        assertThat(indexPrefix.captured).isEqualTo("Project/000005/Report/Partner/000001/PartnerControlReport/000002/ControlCertificate/")
    }
}
