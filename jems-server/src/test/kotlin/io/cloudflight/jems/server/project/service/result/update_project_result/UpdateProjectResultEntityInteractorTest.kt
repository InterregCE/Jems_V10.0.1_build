package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UpdateProjectResultEntityInteractorTest: UnitTest() {
    private val testProjectResult1 = ProjectResultDTO(
        resultNumber = 1,
        targetValue = "TEST"
    )

    private val testProjectResult2 = ProjectResultDTO(
        resultNumber = 2,
        targetValue = "TEST"
    )

    private val updateTestProjectResult1 = ProjectResultDTO(
        resultNumber = 1,
        targetValue = "TEST updated"
    )

    private val updateTestProjectResult2 = ProjectResultDTO(
        resultNumber = 2,
        targetValue = "TEST updated"
    )

    private val projectResultsUpdated =
        mutableSetOf<ProjectResultDTO>(testProjectResult1, testProjectResult2)

    private val projectResultsToUpdate =
        mutableSetOf<ProjectResultDTO>(updateTestProjectResult1, updateTestProjectResult2)


    @MockK
    lateinit var persistence: ProjectResultPersistence

    @InjectMockKs
    lateinit var updateProjectResult: UpdateProjectResult

    @Test
    fun `delete project results from a project`() {
        every { persistence.updateProjectResults(any(), any()) } returns emptySet<ProjectResultDTO>()
        Assertions.assertThat(updateProjectResult.updateProjectResults(1L, emptySet())).isEmpty()
    }

    @Test
    fun `update project results for a project`() {
        every { persistence.updateProjectResults(1L, any()) } returns projectResultsUpdated
        Assertions.assertThat(
            updateProjectResult.updateProjectResults(
                1L,
                projectResultsToUpdate,
            )
        ).isEqualTo(projectResultsUpdated)
    }

}
