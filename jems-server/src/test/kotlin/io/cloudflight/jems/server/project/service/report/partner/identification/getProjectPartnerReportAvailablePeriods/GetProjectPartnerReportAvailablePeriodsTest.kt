package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportAvailablePeriodsTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L
        private const val PROJECT_ID = 299L
    }

    @MockK
    lateinit var reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportAvailablePeriods

    @Test
    fun get() {
        val period0 = mockk<ProjectPartnerReportPeriod>()
        every { period0.number } returns 0
        val period1 = mockk<ProjectPartnerReportPeriod>()
        every { period1.number } returns 1
        val period255 = mockk<ProjectPartnerReportPeriod>()
        every { period255.number } returns 255

        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns null
        every { contractingMonitoringPersistence.getContractingMonitoring(PROJECT_ID) } returns monitoring

        every { reportIdentificationPersistence.getAvailablePeriods(PARTNER_ID, reportId = 352L) } returns
            listOf(period0, period1, period255)

        assertThat(interactor.get(PARTNER_ID, reportId = 352L)).containsExactly(period1)
    }

}
