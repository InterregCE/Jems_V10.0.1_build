package io.cloudflight.jems.server.project.service.report.project.certificate.getListOfCertificate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

internal class GetListOfCertificateTest : UnitTest() {

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence
    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @InjectMockKs
    lateinit var interactor: GetListOfCertificate

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence, projectReportCertificatePersistence)
    }

    @Test
    fun `listCertificates - default sort`() {
        val partner = mockk<ProjectPartnerDetail>()
        every { partner.id } returns 714L
        every { partnerPersistence.findTop30ByProjectId(18L) } returns listOf(partner)

        val mockResult = mockk<Page<PartnerReportCertificate>>()
        val slotPageable = slot<Pageable>()
        every { projectReportCertificatePersistence.listCertificates(setOf(714L), capture(slotPageable)) } returns mockResult

        val pageable = PageRequest.of(0, 20)
        assertThat(interactor.listCertificates(18L, reportId = 250L, pageable)).isEqualTo(mockResult)
        assertThat(slotPageable.captured.sort.toList()).containsExactly(
            Sort.Order.desc("project_report_id IS NULL"),
            Sort.Order.desc("project_report_id = 250"),
            Sort.Order.desc("id"),
        )
    }

    @Test
    fun `listCertificates - custom sort`() {
        val partner = mockk<ProjectPartnerDetail>()
        every { partner.id } returns 715L
        every { partnerPersistence.findTop30ByProjectId(19L) } returns listOf(partner)

        val mockResult = mockk<Page<PartnerReportCertificate>>()
        val slotPageable = slot<Pageable>()
        every { projectReportCertificatePersistence.listCertificates(setOf(715L), capture(slotPageable)) } returns mockResult

        val pageable = PageRequest.of(0, 20, Sort.by("something else"))
        assertThat(interactor.listCertificates(19L, reportId = 251L, pageable)).isEqualTo(mockResult)
        assertThat(slotPageable.captured.sort.toList()).containsExactly(
            Sort.Order.asc("something else"),
        )
    }

}
