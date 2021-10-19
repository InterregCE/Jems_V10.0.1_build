package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.userApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormUpdate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
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
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class ProjectWorkPackageAuthorizationTest : UnitTest() {

    companion object {
        private const val WORK_PACKAGE_ID = 4L
        private const val INVESTMENT_ID = 7L
        private const val PROJECT_ID = 9L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var workPackageService: WorkPackageService

    @MockK
    lateinit var workPackagePersistence: WorkPackagePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var mockStatus: ApplicationStatus

    @InjectMockKs
    lateinit var projectWorkPackageAuthorization: ProjectWorkPackageAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService)
    }

    @ParameterizedTest(name = "owner, without special permissions for this project, can update workPackage, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `owner, without special permissions for this project, can update workPackage`(isOpen: Boolean) {
        val user = applicantUser
        every { mockStatus.canBeModified() } returns isOpen

        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = user.user.id, projectStatus = mockStatus)
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))

        assertThat(projectWorkPackageAuthorization.canUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isEqualTo(isOpen)
    }

    @ParameterizedTest(name = "user with permission for THIS PROJECT, not owner, can update workPackage, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `user with permission for THIS PROJECT, not owner, can update workPackage`(isOpen: Boolean) {
        val user = LocalCurrentUser(userApplicant.copy(assignedProjects = setOf(PROJECT_ID)), "hash_pass", applicantUser.authorities union setOf(SimpleGrantedAuthority(ProjectFormUpdate.name)))
        every { mockStatus.canBeModified() } returns isOpen

        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = user.user.id, projectStatus = mockStatus)
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).contains(PROJECT_ID)
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormUpdate.name))

        assertThat(projectWorkPackageAuthorization.canUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isEqualTo(isOpen)
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

    @Test
    fun `user NOT owner, without permission, cannot submit, he does NOT have Retrieve also`() {
        val user = applicantUser

        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns ProjectApplicantAndStatus(PROJECT_ID, applicantId = 3482L, projectStatus = mockStatus)
        every { securityService.currentUser } returns user
        every { securityService.getUserIdOrThrow() } returns user.user.id

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))

        assertThrows<ResourceNotFoundException> { projectWorkPackageAuthorization.canUpdateProjectWorkPackage(WORK_PACKAGE_ID) }
    }

    @Test
    fun `project LATEST version, user HAS permissions, can retrieve workPackage`() {
        val user = LocalCurrentUser(userApplicant.copy(assignedProjects = setOf(12L)), "hash_pass", applicantUser.authorities union setOf(SimpleGrantedAuthority(ProjectFormRetrieve.name)))

        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns ProjectApplicantAndStatus(12L, applicantId = 3210L, projectStatus = mockStatus)
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).contains(12L)
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormRetrieve.name))

        assertThat(projectWorkPackageAuthorization.canRetrieveProjectWorkPackage(12L, WORK_PACKAGE_ID)).isTrue
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

    @Test
    fun `project HISTORIC version, user HAS NOT permissions, he is OWNER, can retrieve workPackage`() {
        val user = applicantUser

        every { projectPersistence.getApplicantAndStatusById(13L) } returns ProjectApplicantAndStatus(13L, applicantId = user.user.id, projectStatus = mockStatus)
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormRetrieve.name))

        assertThat(projectWorkPackageAuthorization.canRetrieveProjectWorkPackage(13L, WORK_PACKAGE_ID, "1.0")).isTrue
    }

    @Test
    fun `project HISTORIC version, user HAS NOT permissions, and he is NOT an OWNER, CANNOT retrieve workPackage`() {
        val user = applicantUser

        every { projectPersistence.getApplicantAndStatusById(13L) } returns ProjectApplicantAndStatus(13L, applicantId = 2056L, projectStatus = mockStatus)
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormRetrieve.name))

        assertThat(projectWorkPackageAuthorization.canRetrieveProjectWorkPackage(13L, WORK_PACKAGE_ID, "1.0")).isFalse
    }

}
