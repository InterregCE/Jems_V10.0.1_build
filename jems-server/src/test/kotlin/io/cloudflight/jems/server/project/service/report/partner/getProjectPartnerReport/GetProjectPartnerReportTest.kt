package io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 588L
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var getReport: GetProjectPartnerReport

    @Test
    fun findById() {
        val report = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 14) } returns report
        assertThat(getReport.findById(PARTNER_ID, 14L).equals(report)).isTrue
    }

    @Test
    fun findAll() {
        val report = mockk<ProjectPartnerReportSummary>()
        every { reportPersistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()) } returns PageImpl(listOf(report))
        assertThat(getReport.findAll(PARTNER_ID, Pageable.unpaged()).content).containsExactly(report)
    }

}
