package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional.empty
import java.util.Optional.of

internal class GetProjectPartnerReportIdentificationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L

        private val emptyIdentification = ProjectPartnerReportIdentification(
            startDate = null,
            endDate = null,
            period = null,
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            targetGroups = emptyList(),
        )
    }

    @MockK
    lateinit var identificationPersistence: ProjectReportIdentificationPersistence

    @InjectMockKs
    lateinit var getReportIdentification: GetProjectPartnerReportIdentification

    @Test
    fun getForPartner() {
        val identification = mockk<ProjectPartnerReportIdentification>()
        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 225L) } returns of(identification)
        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 225L)).isEqualTo(identification)
    }

    @Test
    fun `getForPartner - empty`() {
        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 225L) } returns empty()
        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 225L)).isEqualTo(emptyIdentification)
    }
}
