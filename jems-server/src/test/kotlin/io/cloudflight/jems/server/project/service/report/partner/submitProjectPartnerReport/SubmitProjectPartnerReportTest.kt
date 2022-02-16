package io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

internal class SubmitProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 579L
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var submitReport: SubmitProjectPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
    }

    @Test
    fun submit() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft

        val submissionTime = slot<ZonedDateTime>()

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 35L) } returns report
        every { reportPersistence.submitReportById(any(), any(), capture(submissionTime)) } returns mockk()
        submitReport.submit(PARTNER_ID, 35L)

        verify(exactly = 1) { reportPersistence.submitReportById(PARTNER_ID, 35L, any()) }
        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))
    }

    @Test
    fun `submit - report is not draft`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Submitted

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 36L) } returns report

        assertThrows<ReportAlreadyClosed> { submitReport.submit(PARTNER_ID, 36L) }
        verify(exactly = 0) { reportPersistence.submitReportById(any(), any(), any()) }
    }

}
