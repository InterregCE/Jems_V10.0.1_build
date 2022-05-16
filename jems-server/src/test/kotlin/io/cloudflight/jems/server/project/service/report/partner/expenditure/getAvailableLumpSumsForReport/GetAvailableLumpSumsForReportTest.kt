package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetAvailableLumpSumsForReportTest : UnitTest() {

    private val PARTNER_ID = 466L

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableLumpSumsForReport

    @Test
    fun getLumpSums() {
        val lumpSums = mockk<List<ProjectPartnerReportLumpSum>>()
        every { reportExpenditurePersistence.getAvailableLumpSums(PARTNER_ID, 10L) } returns lumpSums
        assertThat(interactor.getLumpSums(PARTNER_ID, 10L)).isEqualTo(lumpSums)
    }

}
