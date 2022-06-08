package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.getProjectReportPartnerList.GetProjectReportPartnerListInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

internal class ProjectReportControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L

        private val projectSummary = ProjectPartnerSummary(
            id = 1,
            abbreviation = "abbr",
            active = true,
            role = ProjectPartnerRole.PARTNER
        )
    }

    @MockK
    lateinit var getProjectPartnerReporting: GetProjectReportPartnerListInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportController

    @Test
    fun `should return list of project partners used in reporting`() {
        val projectPartnerReports = listOf(projectSummary)
        every { getProjectPartnerReporting.findAllByProjectId(PROJECT_ID, any()) } returns projectPartnerReports
        assertThat(controller.getProjectPartnersForReporting(PROJECT_ID, Sort.unsorted()).get(0))
            .usingRecursiveComparison()
            .isEqualTo(projectPartnerReports.get(0))
    }
}
