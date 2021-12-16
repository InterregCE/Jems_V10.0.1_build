package io.cloudflight.jems.server.user.service.userproject.get_users_assigned_to_projects

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.model.assignment.ProjectWithUsers
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetUsersAssignedToProjectsTest : UnitTest() {

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @InjectMockKs
    lateinit var getUsersAssigned: GetUsersAssignedToProjects

    @Test
    fun getUserIdsForProject() {
        every { userProjectPersistence.getUserIdsForProject(12L) } returns setOf(4L, 5L, 6L)
        every { projectPersistence.getProjects(Pageable.unpaged()) } returns PageImpl(listOf(
            ProjectSummary(id = 12L, callName = "call name", customIdentifier = "project", acronym = "project acronym", status = ApplicationStatus.DRAFT),
        ))
        assertThat(getUsersAssigned.getProjectsWithAssignedUsers(Pageable.unpaged()).content).containsExactly(
            ProjectWithUsers(id = 12L, "project", "project acronym", projectStatus = ApplicationStatus.DRAFT, assignedUserIds = setOf(4L, 5L, 6L))
        )
    }

}
