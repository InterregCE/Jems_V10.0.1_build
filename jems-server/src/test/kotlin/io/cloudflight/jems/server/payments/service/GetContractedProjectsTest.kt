package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.getContractedProjects.GetContractedProjects
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import java.time.ZonedDateTime

internal class GetContractedProjectsTest : UnitTest() {

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var getContractedProjects: GetContractedProjects

    private val contractedProjectSummary = ProjectSummary(
        id = 8L,
        customIdentifier = "01",
        callName = "call name",
        acronym = "ACR",
        status = ApplicationStatus.CONTRACTED,
        firstSubmissionDate = ZonedDateTime.parse("2021-05-01T10:00:00+02:00"),
        lastResubmissionDate = ZonedDateTime.parse("2021-05-14T23:30:00+02:00"),
        specificObjectiveCode = "SO1.1",
        programmePriorityCode = "P1",
    )

    @Test
    fun getContractedProjects() {
        every { projectPersistence.getProjects(any(), any()) } returns PageImpl(listOf(contractedProjectSummary))
        Assertions.assertThat(getContractedProjects.getContractedProjects(" ")).isEqualTo(PageImpl(listOf(contractedProjectSummary)))
    }
}
