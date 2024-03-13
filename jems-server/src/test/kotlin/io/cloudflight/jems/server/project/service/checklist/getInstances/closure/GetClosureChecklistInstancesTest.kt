package io.cloudflight.jems.server.project.service.checklist.getInstances.closure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.checklist.ClosureChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetClosureChecklistInstancesTest: UnitTest() {

    private val projectId = 3L
    private val reportId = 4L

    @RelaxedMockK
    private lateinit var persistence: ClosureChecklistInstancePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var getClosureChecklistInstances: GetClosureChecklistInstances

    @BeforeEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `get all closure checklist instances`() {
        every { projectReportPersistence.getReportById(projectId, reportId).id } returns reportId
        val searchRequest = slot<ChecklistInstanceSearchRequest>()
        getClosureChecklistInstances.getClosureChecklistInstances(projectId, reportId)

        verify { persistence.findChecklistInstances(capture(searchRequest)) }
        assertThat(searchRequest.captured.relatedToId).isEqualTo(reportId)
        assertThat(searchRequest.captured.type).isEqualTo(ProgrammeChecklistType.CLOSURE)
    }

}
