package io.cloudflight.jems.server.project.controller.report.project.certificate

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.certificate.PartnerReportCertificateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate.DeselectCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.certificate.getListOfCertificate.GetListOfCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate.SelectCertificateInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class ProjectReportCertificateControllerTest : UnitTest() {

    private val YESTERDAY = ZonedDateTime.now().minusDays(1)

    private val certificate = PartnerReportCertificate(
        partnerReportId = 52L,
        partnerReportNumber = 6,
        partnerId = 252L,
        partnerRole = ProjectPartnerRole.PARTNER,
        partnerNumber = 250,
        totalEligibleAfterControl = BigDecimal.valueOf(8645L, 2),
        controlEnd = YESTERDAY,
        projectReportId = 64L,
        projectReportNumber = 17,
    )

    private val expectedCertificate = PartnerReportCertificateDTO(
        partnerReportId = 52L,
        partnerReportNumber = 6,
        partnerId = 252L,
        partnerRole = ProjectPartnerRoleDTO.PARTNER,
        partnerNumber = 250,
        totalEligibleAfterControl = BigDecimal.valueOf(8645L, 2),
        controlEnd = YESTERDAY,
        projectReportId = 64L,
        projectReportNumber = 17,
        disabled = true,
        checked = false,
    )

    @MockK
    private lateinit var getListOfCertificate: GetListOfCertificateInteractor
    @MockK
    private lateinit var deselectCertificate: DeselectCertificateInteractor
    @MockK
    private lateinit var selectCertificate: SelectCertificateInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportCertificateController

    @BeforeEach
    fun resetMocks() {
        clearMocks(getListOfCertificate, deselectCertificate, selectCertificate)
    }

    @Test
    fun getProjectReportListOfCertificate() {
        every { getListOfCertificate.listCertificates(15L, reportId = 58L, Pageable.unpaged()) } returns PageImpl(listOf(
            certificate.copy(partnerReportId = 14L, projectReportId = 57L),
            certificate.copy(partnerReportId = 15L, projectReportId = 58L),
            certificate.copy(partnerReportId = 16L, projectReportId = null),
        ))
        assertThat(controller.getProjectReportListOfCertificate(15L, reportId = 58L, Pageable.unpaged())).containsExactly(
            expectedCertificate.copy(partnerReportId = 14L, projectReportId = 57L, disabled = true, checked = false),
            expectedCertificate.copy(partnerReportId = 15L, projectReportId = 58L, disabled = false, checked = true),
            expectedCertificate.copy(partnerReportId = 16L, projectReportId = null, disabled = false, checked = false),
        )
    }

    @Test
    fun deselectCertificate() {
        every { deselectCertificate.deselectCertificate(16L, reportId = 60L, certificateId = 6489L) } answers { }
        controller.deselectCertificate(16L, reportId = 60L, certificateId = 6489L)
        verify(exactly = 1) { controller.deselectCertificate(16L, reportId = 60L, certificateId = 6489L) }
    }

    @Test
    fun selectCertificate() {
        every { selectCertificate.selectCertificate(17L, reportId = 62L, certificateId = 6490L) } answers { }
        controller.selectCertificate(17L, reportId = 62L, certificateId = 6490L)
        verify(exactly = 1) { controller.selectCertificate(17L, reportId = 62L, certificateId = 6490L) }
    }

}
