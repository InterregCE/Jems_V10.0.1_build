package io.cloudflight.jems.server.project.controller.partner.report

import io.cloudflight.jems.server.project.service.partner.getProjectPartnerReporting.GetProjectPartnerReportingInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Sort

@ExtendWith(MockKExtension::class)
class ProjectPartnerReportControllerTest {

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
    lateinit var getProjectPartnerReporting: GetProjectPartnerReportingInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportController


    @Test
    fun `should return list of project partners used in reporting`() {
        val projectPartnerReports = listOf(projectSummary)
        every { getProjectPartnerReporting.findAllByProjectId(PROJECT_ID, any()) } returns projectPartnerReports
        assertThat(controller.getProjectPartnersForReporting(PROJECT_ID, Sort.unsorted()).get(0))
            .usingRecursiveComparison()
            .isEqualTo(projectPartnerReports.get(0))
    }
}
