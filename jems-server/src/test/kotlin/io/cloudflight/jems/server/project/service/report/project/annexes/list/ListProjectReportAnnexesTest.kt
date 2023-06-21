package io.cloudflight.jems.server.project.service.report.project.annexes.list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileSearchRequest
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.InvalidSearchConfiguration
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.InvalidSearchFilterConfiguration
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ListProjectReportAnnexesTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: ListProjectReportAnnexes

    @Test
    fun `should list files uploaded to project report annexes (root)`() {
        testValidConfig(
            projectId = 1L,
            reportId = 2L,
            type = JemsFileType.ProjectReport,
            filterSubtypes = setOf(
                JemsFileType.ProjectReport,
                JemsFileType.ActivityProjectReport,
                JemsFileType.DeliverableProjectReport,
                JemsFileType.OutputProjectReport,
                JemsFileType.ProjectResult
            ),
            expectedIndexSearch = "Project/000001/Report/ProjectReport/000002/"
        )
    }

    @Test
    fun `should list files uploaded in the 'work plan' tab of the project report`() {
        testValidConfig(
            projectId = 1L,
            reportId = 2L,
            type = JemsFileType.WorkPlanProjectReport,
            filterSubtypes = setOf(
                JemsFileType.ActivityProjectReport,
                JemsFileType.DeliverableProjectReport,
                JemsFileType.OutputProjectReport
            ),
            expectedIndexSearch = "Project/000001/Report/ProjectReport/000002/WorkPlanProjectReport/"
        )
    }

    @Test
    fun `should list files uploaded in the 'project result' tab of the project report`() {
        testValidConfig(
            projectId = 1L,
            reportId = 2L,
            type = JemsFileType.ProjectResult,
            filterSubtypes = setOf(JemsFileType.ProjectResult),
            expectedIndexSearch = "Project/000001/Report/ProjectReport/000002/ProjectResult/"
        )
    }

    @Test
    fun `should throw InvalidSearchConfiguration when the filter is not valid`() {
        val searchRequest = JemsFileSearchRequest(
            reportId = 2L,
            treeNode = JemsFileType.WorkPlan,
        )

        assertThrows<InvalidSearchConfiguration> { interactor.list(1L, 2L, Pageable.unpaged(), searchRequest) }
    }

    @Test
    fun `should throw InvalidSearchFilterConfiguration when the search filter configuration is not valid`() {
        val searchRequest = JemsFileSearchRequest(
            reportId = 2L,
            treeNode = JemsFileType.ProjectReport,
            // filter subtypes are not actually subtypes of the given treeNode
            filterSubtypes = setOf(JemsFileType.WorkPlan, JemsFileType.Project),
        )
        val ex = assertThrows<InvalidSearchFilterConfiguration> {
            interactor.list(1L, 2L, Pageable.unpaged(), searchRequest)
        }

        assertThat(ex.message).isEqualTo("Following filters cannot be applied: [WorkPlan, Project]")
    }

    private fun testValidConfig(
        projectId: Long,
        reportId: Long,
        type: JemsFileType,
        filterSubtypes: Set<JemsFileType>,
        expectedIndexSearch: String
    ) {
        val projectReportFile = mockk<JemsFile>()

        val indexPrefix = slot<String>()
        val filterSubtypesSlot = slot<Set<JemsFileType>>()
        val filterUsers = slot<Set<Long>>()
        every {
            filePersistence.listAttachments(
                any(),
                capture(indexPrefix),
                capture(filterSubtypesSlot),
                capture(filterUsers)
            )
        } returns PageImpl(listOf(projectReportFile))

        val searchRequest = JemsFileSearchRequest(
            reportId = reportId,
            treeNode = type,
            filterSubtypes = filterSubtypes
        )

        assertThat(interactor.list(projectId, reportId, Pageable.unpaged(), searchRequest).content)
            .containsExactly(projectReportFile)

        assertThat(indexPrefix.captured).isEqualTo(expectedIndexSearch)
        assertThat(filterSubtypesSlot.captured).containsExactlyElementsOf(filterSubtypes)
        assertThat(filterUsers.captured).isEmpty()
    }
}
