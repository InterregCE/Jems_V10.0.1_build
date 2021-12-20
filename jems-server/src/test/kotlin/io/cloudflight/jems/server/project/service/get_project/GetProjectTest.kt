package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.userApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

internal class GetProjectTest : UnitTest() {

    companion object {
        val startDate: ZonedDateTime = ZonedDateTime.now().minusDays(2)
        val endDate: ZonedDateTime = ZonedDateTime.now().plusDays(2)

        val callSettings = ProjectCallSettings(
            callId = 15,
            callName = "Call 15",
            startDate = startDate,
            endDate = endDate,
            endDateStep1 = null,
            lengthOfPeriod = 6,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            stateAids = emptyList(),
            applicationFormFieldConfigurations = mutableSetOf()
        )

        val dummyProject = ProjectSummary(
            id = 8L,
            customIdentifier = "01",
            callName = "call name",
            acronym = "ACR",
            status = ApplicationStatus.SUBMITTED,
            firstSubmissionDate = ZonedDateTime.parse("2021-05-01T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2021-05-14T23:30:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1",
        )
    }

    @MockK
    lateinit var persistence: ProjectPersistence

    @MockK
    lateinit var collaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getProject: GetProject

    @Test
    fun getProjectCallSettings() {
        every { persistence.getProjectCallSettings(1L) } returns callSettings
        assertThat(getProject.getProjectCallSettings(1L)).isEqualTo(callSettings.copy())
    }

    @Test
    fun getMyProjects() {
        val user = LocalCurrentUser(userApplicant.copy(id = 44L, assignedProjects = setOf(10L, 11L)), "hash_pass", emptySet())

        val extraProjectIds = slot<Set<Long>>()
        every { persistence.getProjectsOfUserPlusExtra(Pageable.unpaged(), 44L, capture(extraProjectIds)) } returns PageImpl(listOf(dummyProject))
        every { securityService.currentUser } returns user
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { collaboratorPersistence.getProjectIdsForUser(44L) } returns setOf(11L, 12L)

        assertThat(getProject.getMyProjects(Pageable.unpaged()).content).containsExactly(dummyProject)
        assertThat(extraProjectIds.captured).containsExactlyInAnyOrder(10L, 11L, 12L)
    }

}
