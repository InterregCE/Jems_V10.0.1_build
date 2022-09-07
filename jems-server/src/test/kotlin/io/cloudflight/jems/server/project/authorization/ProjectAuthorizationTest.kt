package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.userApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class ProjectAuthorizationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 598L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var mockStatus: ApplicationStatus

    @InjectMockKs
    lateinit var projectAuthorization: ProjectAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService)
    }

    @Test
    fun `user is owner`() {
        every { securityService.getUserIdOrThrow() } returns adminUser.user.id
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID,
                applicantId = adminUser.user.id,
                projectStatus = SUBMITTED,
                collaboratorManageIds = setOf(adminUser.user.id),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        assertThat(projectAuthorization.isUserViewCollaboratorForProjectOrThrow(PROJECT_ID)).isTrue
    }

    @Test
    fun `user is not owner`() {
        every { securityService.getUserIdOrThrow() } returns adminUser.user.id
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID,
                applicantId = 3658L,
                projectStatus = SUBMITTED,
                collaboratorManageIds = setOf(9896L),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        assertThrows<ResourceNotFoundException> { projectAuthorization.isUserViewCollaboratorForProjectOrThrow(PROJECT_ID) }
    }

    @ParameterizedTest(name = "can update project - no permissions and no owner (status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `can update project - no permissions and no owner`(status: ApplicationStatus) {
        val user = programmeUser
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID,
                applicantId = 2478L,
                projectStatus = status,
                collaboratorManageIds = setOf(2478L),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThrows<ResourceNotFoundException> { projectAuthorization.canUpdateProject(PROJECT_ID, true) }
        assertThrows<ResourceNotFoundException> { projectAuthorization.canUpdateProject(PROJECT_ID, false) }
    }

    @ParameterizedTest(name = "can update project - also after approved - OWNER, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update project - also after approved - OWNER`(isOpen: Boolean) {
        val projectId = 598L
        val user = applicantUser
        every { mockStatus.canBeModified() } returns isOpen

        every { projectPersistence.getApplicantAndStatusById(projectId) } returns
            ProjectApplicantAndStatus(projectId,
                applicantId = user.user.id,
                projectStatus = mockStatus,
                collaboratorManageIds = setOf(user.user.id),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectAuthorization.canUpdateProject(projectId, false)).isEqualTo(isOpen)
    }

    @ParameterizedTest(name = "can update project - only before approved - OWNER, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update project - only before approved - OWNER`(isOpen: Boolean) {
        val projectId = 598L
        val user = applicantUser
        every { mockStatus.isModifiableStatusBeforeApproved() } returns isOpen

        every { projectPersistence.getApplicantAndStatusById(projectId) } returns
            ProjectApplicantAndStatus(projectId,
                applicantId = user.user.id,
                projectStatus = mockStatus,
                collaboratorManageIds = setOf(user.user.id),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectAuthorization.canUpdateProject(projectId, true)).isEqualTo(isOpen)
    }

    @ParameterizedTest(name = "can update project - HAS PERMISSION FOR just this PROJECT, no-owner, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update project - HAS PERMISSION FOR just this PROJECT, no-owner`(isOpen: Boolean) {
        val user = LocalCurrentUser(userApplicant.copy(assignedProjects = setOf(PROJECT_ID)), "hash_pass", applicantUser.authorities union setOf(SimpleGrantedAuthority(ProjectFormUpdate.name)))
        every { mockStatus.canBeModified() } returns isOpen

        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID,
                applicantId = 2698L,
                projectStatus = mockStatus,
                collaboratorManageIds = emptySet(),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        // verify test setup
        assertThat(user.user.assignedProjects).contains(PROJECT_ID)
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectAuthorization.canUpdateProject(PROJECT_ID, false)).isEqualTo(isOpen)
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

    @ParameterizedTest(name = "can update project - HAS PERMISSION FOR ALL PROJECTS, no-owner, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update project - HAS PERMISSION FOR ALL PROJECTS, no-owner`(isOpen: Boolean) {
        val user = LocalCurrentUser(userApplicant, "hash_pass", applicantUser.authorities union setOf(
            SimpleGrantedAuthority(ProjectFormUpdate.name),
            SimpleGrantedAuthority(ProjectRetrieve.name),
        ))
        every { mockStatus.canBeModified() } returns isOpen

        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID,
                applicantId = 2698L,
                projectStatus = mockStatus,
                collaboratorManageIds = emptySet(),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectAuthorization.canUpdateProject(PROJECT_ID, false)).isEqualTo(isOpen)
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

}
