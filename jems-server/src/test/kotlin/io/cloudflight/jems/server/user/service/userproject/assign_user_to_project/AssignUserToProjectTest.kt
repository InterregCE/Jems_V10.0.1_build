package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UpdateProjectUser
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileApplicationRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCheckApplicationForm
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectAssessmentView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusDecisionRevert
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusReturnToApplicant
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStartStepTwo
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class AssignUserToProjectTest : UnitTest() {

    companion object {
        private const val USER_ADMIN_ID = 22L
        private const val USER_PROGRAMME_ID = 23L
        private const val USER_MONITOR_ID_1 = 24L
        private const val USER_MONITOR_ID_2 = 25L
        private const val USER_APPLICANT_ID = 26L

        private const val APPLICANT_ROLE_ID = 455L
        private const val MONITOR_ROLE_ID = 456L
        private const val ADMIN_ROLE_ID = 457L
        private const val PROGRAMME_ROLE_ID = 458L

        private fun role(id: Long) = UserRoleSummary(id, "role", false)

        private fun project(id: Long) = ProjectSummary(id, "cid", "call", "acronym", ApplicationStatus.STEP1_DRAFT)

        val admin = UserSummary(USER_ADMIN_ID, "admin", "", "", role(ADMIN_ROLE_ID), UserStatus.ACTIVE)
        val programmeUser = UserSummary(USER_PROGRAMME_ID, "programme", "", "", role(PROGRAMME_ROLE_ID), UserStatus.ACTIVE)
        val monitorUser_1 = UserSummary(USER_MONITOR_ID_1, "monitor", "", "", role(MONITOR_ROLE_ID), UserStatus.ACTIVE)
        val monitorUser_2 = UserSummary(USER_MONITOR_ID_2, "monitor", "", "", role(MONITOR_ROLE_ID), UserStatus.ACTIVE)
        val applicantUser = UserSummary(USER_APPLICANT_ID, "applicant", "", "", role(APPLICANT_ROLE_ID), UserStatus.ACTIVE)
    }

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var userRolePersistence: UserRolePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var assignUserToProject: AssignUserToProject

    @Test
    fun updateUserAssignmentsOnProject() {
        val permisisonsToHave = mutableListOf<Set<UserRolePermission>>()
        val permissionsNotToHave = mutableListOf<Set<UserRolePermission>>()

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(capture(permisisonsToHave), capture(permissionsNotToHave)) } returns
            setOf(ADMIN_ROLE_ID, PROGRAMME_ROLE_ID) andThen setOf(MONITOR_ROLE_ID)
        every { userPersistence.findAllWithRoleIdIn(setOf(ADMIN_ROLE_ID, PROGRAMME_ROLE_ID)) } returns listOf(admin, programmeUser)

        every { userPersistence.findAllByIds(setOf(USER_ADMIN_ID, USER_PROGRAMME_ID, USER_MONITOR_ID_1, USER_MONITOR_ID_2, USER_APPLICANT_ID)) } returns listOf(admin, programmeUser, monitorUser_1, monitorUser_2, applicantUser)
        every { userPersistence.findAllByIds(setOf(USER_MONITOR_ID_1)) } returns listOf(monitorUser_1)
        every { userPersistence.findAllByIds(setOf(USER_MONITOR_ID_2)) } returns listOf(monitorUser_2)

        val projectIds = mutableListOf<Long>()
        val userIdsToRemove = mutableListOf<Set<Long>>()
        val userIdsToAdd = mutableListOf<Set<Long>>()
        every { userProjectPersistence.changeUsersAssignedToProject(capture(projectIds), capture(userIdsToRemove), capture(userIdsToAdd)) } returns
            setOf(USER_MONITOR_ID_1) andThen setOf(USER_MONITOR_ID_2)

        every { projectPersistence.getProjectSummary(1L) } returns project(1L)
        every { projectPersistence.getProjectSummary(2L) } returns project(2L)

        assignUserToProject.updateUserAssignmentsOnProject(data = setOf(
            UpdateProjectUser(
                projectId = 1L,
                userIdsToAdd = setOf(USER_ADMIN_ID, USER_MONITOR_ID_1),
                userIdsToRemove = setOf(1000L, 1001L),
            ),
            UpdateProjectUser(
                projectId = 2L,
                userIdsToAdd = setOf(USER_PROGRAMME_ID, USER_MONITOR_ID_2, USER_APPLICANT_ID),
                userIdsToRemove = emptySet(),
            ),
            UpdateProjectUser(
                projectId = 3L,
                userIdsToAdd = emptySet(),
                userIdsToRemove = emptySet(),
            ),
        ))

        assertThat(projectIds).containsExactly(1L, 2L)
        assertThat(userIdsToRemove[0]).containsExactly(1000L, 1001L)
        assertThat(userIdsToAdd[0]).containsExactly(USER_MONITOR_ID_1)
        assertThat(userIdsToRemove[1]).isEmpty()
        assertThat(userIdsToAdd[1]).containsExactly(USER_MONITOR_ID_2)

        val slotAudit = mutableListOf<AssignUserEvent>()
        verify(exactly = 2) { eventPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit[0].project.id).isEqualTo(1L)
        assertThat(slotAudit[0].users).containsExactly(admin, programmeUser, monitorUser_1)
        assertThat(slotAudit[1].project.id).isEqualTo(2L)
        assertThat(slotAudit[1].users).containsExactly(admin, programmeUser, monitorUser_2)

        // just few extra assertions to make sure test is running like expected:

        // firstly it was called for users with ProjectRetrieve permissions
        assertThat(permisisonsToHave[0]).containsExactlyInAnyOrder(ProjectRetrieve, ProjectRetrieveEditUserAssignments)
        assertThat(permissionsNotToHave[0]).isEmpty()
        // then for monitor users
        assertThat(permisisonsToHave[1]).containsExactlyInAnyOrder(ProjectFormRetrieve, ProjectFileApplicationRetrieve, ProjectCheckApplicationForm, ProjectAssessmentView, ProjectStatusDecisionRevert, ProjectStatusReturnToApplicant, ProjectStartStepTwo, ProjectFileAssessmentRetrieve)
        assertThat(permissionsNotToHave[1]).containsExactlyInAnyOrder(ProjectRetrieve, ProjectRetrieveEditUserAssignments)
    }

}
