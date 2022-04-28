package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class ProjectChecklistAuthorizationTest : UnitTest() {

    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val createChecklist = CreateChecklistInstanceModel(
        RELATED_TO_ID,
        PROGRAMME_CHECKLIST_ID
    )

    val adminUserWithChecklistPermission = LocalCurrentUser(
        AuthorizationUtil.userAdmin, "hash_pass", listOf(
            SimpleGrantedAuthority("ROLE_" + AuthorizationUtil.userAdmin.userRole.name),
            SimpleGrantedAuthority(UserRolePermission.ProgrammeSetupRetrieve.key),
            SimpleGrantedAuthority(UserRolePermission.ProgrammeSetupUpdate.key),
            SimpleGrantedAuthority(UserRolePermission.ProjectFormUpdate.key),
            SimpleGrantedAuthority(UserRolePermission.ProjectAssessmentChecklistUpdate.key)
        )
    )

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userProjectPersistenceProvider: UserProjectPersistenceProvider

    @InjectMockKs
    lateinit var projectChecklistAuthorization: ProjectChecklistAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService)
    }

    @Test
    fun `user has ProjectAssessmentChecklistUpdate permission and also assigned to project in EDIT level`() {
        every { securityService.getUserIdOrThrow() } returns adminUserWithChecklistPermission.user.id
        every { userProjectPersistenceProvider.getUserIdsForProject(RELATED_TO_ID) } returns emptySet()
        every { securityService.currentUser } returns adminUserWithChecklistPermission
        every { projectPersistence.getApplicantAndStatusById(RELATED_TO_ID) } returns
            ProjectApplicantAndStatus(
                RELATED_TO_ID,
                applicantId = adminUserWithChecklistPermission.user.id,
                projectStatus = SUBMITTED,
                collaboratorManageIds = setOf(adminUserWithChecklistPermission.user.id),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        assertThat(
            projectChecklistAuthorization.hasPermissionOrIsEditCollaborator(
                UserRolePermission.ProjectAssessmentChecklistUpdate, createChecklist
            )
        ).isTrue
    }

    @Test
    fun `user has no ProjectAssessmentChecklistUpdate permission`() {
        every { securityService.getUserIdOrThrow() } returns adminUser.user.id
        every { userProjectPersistenceProvider.getUserIdsForProject(RELATED_TO_ID) } returns emptySet()
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(RELATED_TO_ID) } returns
            ProjectApplicantAndStatus(
                RELATED_TO_ID,
                applicantId = adminUser.user.id,
                projectStatus = SUBMITTED,
                collaboratorManageIds = setOf(adminUser.user.id),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        assertThrows<ResourceNotFoundException> {
            projectChecklistAuthorization.hasPermissionOrIsEditCollaborator(
                UserRolePermission.ProjectAssessmentChecklistUpdate,
                createChecklist
            )
        }
    }


    @Test
    fun `user has ProjectAssessmentChecklistUpdate permission but is not assigned to project in EDIT level`() {
        every { securityService.getUserIdOrThrow() } returns adminUserWithChecklistPermission.user.id
        every { userProjectPersistenceProvider.getUserIdsForProject(RELATED_TO_ID) } returns emptySet()
        every { securityService.currentUser } returns adminUserWithChecklistPermission
        every { projectPersistence.getApplicantAndStatusById(RELATED_TO_ID) } returns
            ProjectApplicantAndStatus(
                RELATED_TO_ID,
                applicantId = adminUserWithChecklistPermission.user.id,
                projectStatus = SUBMITTED,
                collaboratorManageIds = emptySet(),
                collaboratorEditIds = emptySet(),
                collaboratorViewIds = emptySet(),
            )

        assertThrows<ResourceNotFoundException> {
            projectChecklistAuthorization.hasPermissionOrIsEditCollaborator(
                UserRolePermission.ProjectAssessmentChecklistUpdate,
                createChecklist
            )
        }
    }

}
