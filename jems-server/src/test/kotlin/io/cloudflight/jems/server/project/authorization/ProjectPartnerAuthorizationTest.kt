package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class ProjectPartnerAuthorizationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 650L
        private const val PROJECT_ID = 21L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var mockStatus: ApplicationStatus

    @InjectMockKs
    lateinit var projectPartnerAuthorization: ProjectPartnerAuthorization

    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, null) } returns PROJECT_ID
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService)
    }

    // Can Update:

    @ParameterizedTest(name = "can update partner - no permissions and no owner (status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `can update partner - no permissions and no owner`(status: ApplicationStatus) {
        val user = programmeUser
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = 1178L, projectStatus = status)

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThrows<ResourceNotFoundException> { projectPartnerAuthorization.canUpdatePartner(PARTNER_ID) }
    }

    @ParameterizedTest(name = "can update partner - OWNER, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update partner - OWNER`(isOpen: Boolean) {
        val user = applicantUser
        every { mockStatus.canBeModified() } returns isOpen

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = user.user.id, projectStatus = mockStatus)
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(applicantUser.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(applicantUser.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectPartnerAuthorization.canUpdatePartner(PARTNER_ID)).isEqualTo(isOpen)
    }


    @ParameterizedTest(name = "can update partner - HAS PERMISSION FOR just this PROJECT, no-owner, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update partner - HAS PERMISSION FOR just this PROJECT, no-owner`(isOpen: Boolean) {
        val user = LocalCurrentUser(
            AuthorizationUtil.userApplicant.copy(assignedProjects = setOf(PROJECT_ID)), "hash_pass", applicantUser.authorities union setOf(SimpleGrantedAuthority(ProjectFormUpdate.name)))
        every { mockStatus.canBeModified() } returns isOpen

        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(PROJECT_ID, applicantId = 2698L, projectStatus = mockStatus)

        // verify test setup
        assertThat(user.user.assignedProjects).contains(PROJECT_ID)
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectPartnerAuthorization.canUpdatePartner(PARTNER_ID)).isEqualTo(isOpen)
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

    @ParameterizedTest(name = "can update partner - HAS PERMISSION FOR ALL PROJECTS, no-owner, isOpen {0}")
    @ValueSource(booleans = [true, false])
    fun `can update partner - HAS PERMISSION FOR ALL PROJECTS, no-owner`(isOpen: Boolean) {
        val user = LocalCurrentUser(AuthorizationUtil.userApplicant, "hash_pass", applicantUser.authorities union setOf(
            SimpleGrantedAuthority(ProjectRetrieve.name),
            SimpleGrantedAuthority(ProjectFormUpdate.name),
        ))
        every { mockStatus.canBeModified() } returns isOpen

        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = 2698L, projectStatus = mockStatus)

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormUpdate.name))
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectPartnerAuthorization.canUpdatePartner(PARTNER_ID)).isEqualTo(isOpen)
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }


    // Can Retrieve:

    @ParameterizedTest(name = "can retrieve partner - no permissions and no owner (status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `can retrieve partner - no permissions and no owner`(status: ApplicationStatus) {
        val user = programmeUser
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = 1178L, projectStatus = status)

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormRetrieve.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThrows<ResourceNotFoundException> { projectPartnerAuthorization.canRetrievePartner(PARTNER_ID) }
    }

    @Test
    fun `can retrieve partner - OWNER`() {
        val user = applicantUser

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = user.user.id, projectStatus = mockStatus)
        every { securityService.getUserIdOrThrow() } returns user.user.id
        every { securityService.currentUser } returns user

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(applicantUser.authorities).doesNotContain(SimpleGrantedAuthority(ProjectFormRetrieve.name))
        assertThat(applicantUser.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectPartnerAuthorization.canRetrievePartner(PARTNER_ID)).isTrue
    }


    @Test
    fun `can retrieve partner - HAS PERMISSION FOR just this PROJECT, no-owner`() {
        val user = LocalCurrentUser(
            AuthorizationUtil.userApplicant.copy(assignedProjects = setOf(PROJECT_ID)), "hash_pass", applicantUser.authorities union setOf(SimpleGrantedAuthority(ProjectFormRetrieve.name)))

        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(PROJECT_ID, applicantId = 2698L, projectStatus = mockStatus)

        // verify test setup
        assertThat(user.user.assignedProjects).contains(PROJECT_ID)
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormRetrieve.name))
        assertThat(user.authorities).doesNotContain(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectPartnerAuthorization.canRetrievePartner(PARTNER_ID)).isTrue
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

    @Test
    fun `can retrieve partner - HAS PERMISSION FOR ALL PROJECTS, no-owner`() {
        val user = LocalCurrentUser(AuthorizationUtil.userApplicant, "hash_pass", applicantUser.authorities union setOf(
            SimpleGrantedAuthority(ProjectFormRetrieve.name),
            SimpleGrantedAuthority(ProjectRetrieve.name),
        ))

        every { securityService.currentUser } returns user
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(PROJECT_ID, applicantId = 2698L, projectStatus = mockStatus)

        // verify test setup
        assertThat(user.user.assignedProjects).isEmpty()
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectFormRetrieve.name))
        assertThat(user.authorities).contains(SimpleGrantedAuthority(ProjectRetrieve.name))

        assertThat(projectPartnerAuthorization.canRetrievePartner(PARTNER_ID)).isTrue
        // user permissions for project are OK, so we are not checking ownership
        verify(exactly = 0) { securityService.getUserIdOrThrow() }
    }

}
