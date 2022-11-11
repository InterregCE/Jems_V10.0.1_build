package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.getContractedProjects.GetContractedProjects
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSearchRequest
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page

internal class GetContractedProjectsTest : UnitTest() {

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var getContractedProjects: GetContractedProjects

    private val expectedSearchRequest = ProjectSearchRequest(
        id = "search-id-key",
        acronym = null,
        firstSubmissionFrom = null,
        firstSubmissionTo = null,
        lastSubmissionFrom = null,
        lastSubmissionTo = null,
        objectives = emptySet(),
        statuses = setOf(ApplicationStatus.CONTRACTED, ApplicationStatus.IN_MODIFICATION),
        calls = emptySet(),
        users = emptySet(),
    )

    @Test
    fun getContractedProjects() {
        val searchSlot = slot<ProjectSearchRequest>()
        val resultMock = mockk<Page<ProjectSummary>>()
        every { projectPersistence.getProjects(any(), capture(searchSlot)) } returns resultMock

        assertThat(getContractedProjects.getContractedProjects("search-id-key")).isEqualTo(resultMock)

        assertThat(searchSlot.captured).isEqualTo(expectedSearchRequest)
    }

}
