package io.cloudflight.jems.server.project.service.projectuser.get_users_assigned_to_projects

import io.cloudflight.jems.api.project.dto.ProjectSearchRequestDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectWithUsersDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSearchRequest
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.model.assignment.ProjectWithUsers
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetUsersAssignedToProjectsTest : UnitTest() {

    companion object {
        val projectSearchRequest = ProjectSearchRequest(
            acronym = "project acronym",
            id = "",
            users = null,
            calls = null,
            firstSubmissionFrom = null,
            firstSubmissionTo = null,
            lastSubmissionFrom = null,
            lastSubmissionTo = null,
            objectives = null,
            statuses = null
        )

        val projectWithUsersDTO = ProjectWithUsersDTO(
            acronym = "project acronym",
            id = null,
            calls = null,
            statuses = null,
            users = null
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @InjectMockKs
    lateinit var getUsersAssigned: GetUsersAssignedToProjects

    @Test
    fun getUserIdsForProject() {
        every { userProjectPersistence.getUserIdsForProject(12L) } returns setOf(4L, 5L, 6L)
        every { projectPersistence.getAssignedProjects(Pageable.unpaged(), projectSearchRequest) } returns PageImpl(listOf(
            ProjectSummary(id = 12L, callName = "call name", customIdentifier = "project", acronym = "project acronym", status = ApplicationStatus.DRAFT),
        ))
        assertThat(getUsersAssigned.getProjectsWithAssignedUsers(Pageable.unpaged(), projectWithUsersDTO).content).containsExactly(
            ProjectWithUsers(id = "12", "project", "project acronym", projectStatus = ApplicationStatus.DRAFT, "call name", users = setOf(4L, 5L, 6L))
        )
    }
}
