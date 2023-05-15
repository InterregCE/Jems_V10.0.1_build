package io.cloudflight.jems.server.project.service.report.partner.base.canCreateProjectPartnerReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CanCreateProjectPartnerReportTest : UnitTest() {

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    private lateinit var interactor: CanCreateProjectPartnerReport

    @Test
    fun canCreateReportFor() {
        every { reportPersistence.existsByStatusIn(101L, ReportStatus.ARE_LAST_OPEN_STATUSES) } returns true
        every { reportPersistence.existsByStatusIn(102L, ReportStatus.ARE_LAST_OPEN_STATUSES) } returns false
        assertThat(interactor.canCreateReportFor(101L)).isFalse()
        assertThat(interactor.canCreateReportFor(102L)).isTrue()
    }

}
