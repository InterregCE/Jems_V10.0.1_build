package io.cloudflight.jems.server.project.service.result.get_project_result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetProjectResultEntityInteractorTest: UnitTest() {
    private val testProjectResult1 = ProjectResultDTO(
        resultNumber = 1,
    )

    private val testProjectResult2 = ProjectResultDTO(
        resultNumber = 2,
    )

    private val projectResults = mutableSetOf<ProjectResultDTO>(
        testProjectResult1,
        testProjectResult2
    )

    @MockK
    lateinit var persistence: ProjectResultPersistence

    @InjectMockKs
    lateinit var getProjectResult: GetProjectResult

    @Test
    fun `get project result outputs returns empty list`() {
        every { persistence.getProjectResultsForProject(any()) } returns emptySet<ProjectResultDTO>()
        Assertions.assertThat(getProjectResult.getProjectResultsForProject(1L)).isEmpty()
    }

    @Test
    fun `get project results`() {
        every { persistence.getProjectResultsForProject(any()) } returns projectResults
        Assertions.assertThat(getProjectResult.getProjectResultsForProject(1L)).isEqualTo(
            projectResults
        )
    }
}
