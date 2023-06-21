package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerCollaboratorEntity
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectCollaboratorEntity
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractsEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractsView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingManagementView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingManagementEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingReportingView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreatorContractingReportingView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreatorContractingReportingEdit
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class projectContractingAuthorizationTest: UnitTest() {

    companion object {
        private const val PARTNER_COLLABORATOR = 111L
        private const val PROJECT_COLLABORATOR = 112L
        private const val COLLABORATOR_EDIT = 113L
        private const val CONTROLLER = 114L
        private const val NOT_RELATED_USER = -1L

        private fun collaborator(userId: Long): UserPartnerCollaboratorEntity {
            val result = mockk<UserPartnerCollaboratorEntity>()
            every { result.id.userId } returns userId
            return result
        }

        private fun collaboratorProj(userId: Long): UserProjectCollaboratorEntity {
            val result = mockk<UserProjectCollaboratorEntity>()
            every { result.id.userId } returns userId
            return result
        }
    }

    @MockK
    lateinit var currentUser: CurrentUser

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var partnerCollaboratorRepository: UserPartnerCollaboratorRepository

    @MockK
    private lateinit var projectCollaboratorRepository: UserProjectCollaboratorRepository

    @MockK
    private lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    private lateinit var authorization: ProjectContractingAuthorization


    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService, partnerPersistence, projectPersistence, partnerCollaboratorRepository,
            projectCollaboratorRepository, controllerInstitutionPersistence)
        every { securityService.currentUser } returns currentUser

        every { partnerCollaboratorRepository.findAllByProjectId(any()) } returns listOf(collaborator(PARTNER_COLLABORATOR))
        every { projectCollaboratorRepository.findAllByIdProjectId(any()) } returns listOf(collaboratorProj(PROJECT_COLLABORATOR))
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(any()) } returns setOf(CONTROLLER)
    }

    private fun mockBehavior(assignedProject: Long?, vararg permission: UserRolePermission) {
        if (assignedProject != null) {
            every { currentUser.user.assignedProjects } returns setOf(assignedProject)
        } else {
            every { currentUser.user.assignedProjects } returns emptySet()
        }

        mockPermissions(*permission)
    }

    private fun mockPermissions(vararg permission: UserRolePermission) {
        permission.forEach {
            every { currentUser.hasPermission(it) } returns true
        }
        setOf(
            ProjectRetrieve,
            ProjectContractsView,
            ProjectContractsEdit,
            ProjectContractingManagementView,
            ProjectContractingManagementEdit,
            ProjectContractingReportingView,
            ProjectCreatorContractingReportingView,
            ProjectContractingReportingEdit,
            ProjectCreatorContractingReportingEdit,
        ).minus(permission).forEach {
            every { currentUser.hasPermission(it) } returns false
        }
    }

    private fun projectWithEditCollaborator(): ProjectApplicantAndStatus {
        val result = mockk<ProjectApplicantAndStatus>()
        every { result.getUserIdsWithEditLevel() } returns setOf(COLLABORATOR_EDIT)
        return result
    }

    @ParameterizedTest(name = "canView - false - is assigned {0})")
    @ValueSource(booleans = [true, false])
    fun `canView - false`(isAssigned: Boolean) {
        val projectId = if (isAssigned) 409L else 410L
        mockBehavior(assignedProject = if (isAssigned) projectId else null)
        every { securityService.getUserIdOrThrow() } returns NOT_RELATED_USER

        assertThat(authorization.canViewContractsAndAgreements(projectId)).isFalse()
        assertThat(authorization.canViewProjectManagers(projectId)).isFalse()
        assertThat(authorization.canViewReportingSchedule(projectId)).isFalse()
    }

    @ParameterizedTest(name = "canView - partner collaborator - is assigned {0})")
    @ValueSource(booleans = [true, false])
    fun `canView - partner collaborator`(isAssigned: Boolean) {
        val projectId = if (isAssigned) 411L else 412L
        mockBehavior(assignedProject = if (isAssigned) projectId else null, ProjectCreatorContractingReportingView)
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR

        assertThat(authorization.canViewContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canViewProjectManagers(projectId)).isTrue()
        assertThat(authorization.canViewReportingSchedule(projectId)).isTrue()
    }

    @ParameterizedTest(name = "canView - project collaborator - is assigned {0})")
    @ValueSource(booleans = [true, false])
    fun `canView - project collaborator`(isAssigned: Boolean) {
        val projectId = if (isAssigned) 413L else 414L
        mockBehavior(assignedProject = if (isAssigned) projectId else null, ProjectCreatorContractingReportingView)
        every { securityService.getUserIdOrThrow() } returns PROJECT_COLLABORATOR

        assertThat(authorization.canViewContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canViewProjectManagers(projectId)).isTrue()
        assertThat(authorization.canViewReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canView - controller`() {
        val projectId = 415L
        mockBehavior(assignedProject = null, ProjectContractsView, ProjectContractingManagementView, ProjectContractingReportingView)
        every { securityService.getUserIdOrThrow() } returns CONTROLLER

        assertThat(authorization.canViewContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canViewProjectManagers(projectId)).isTrue()
        assertThat(authorization.canViewReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canView - monitor user - is assigned`() {
        val projectId = 416L
        mockBehavior(projectId, ProjectContractsView, ProjectContractingManagementView, ProjectContractingReportingView)
        assertThat(authorization.canViewContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canViewProjectManagers(projectId)).isTrue()
        assertThat(authorization.canViewReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canView - monitor user - is not assigned - has retrieve`() {
        val projectId = 417L
        mockBehavior(assignedProject = null,
            ProjectContractsView, ProjectContractingManagementView, ProjectContractingReportingView, ProjectRetrieve)

        assertThat(authorization.canViewContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canViewProjectManagers(projectId)).isTrue()
        assertThat(authorization.canViewReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canView - monitor user - is not assigned - has not retrieve`() {
        val projectId = 418L
        mockBehavior(assignedProject = null,
            ProjectContractsView, ProjectContractingManagementView, ProjectContractingReportingView)
        every { securityService.getUserIdOrThrow() } returns NOT_RELATED_USER

        assertThat(authorization.canViewContractsAndAgreements(projectId)).isFalse()
        assertThat(authorization.canViewProjectManagers(projectId)).isFalse()
        assertThat(authorization.canViewReportingSchedule(projectId)).isFalse()
    }


    @ParameterizedTest(name = "canEdit - false - is assigned {0})")
    @ValueSource(booleans = [true, false])
    fun `canEdit - false`(isAssigned: Boolean) {
        val projectId = if (isAssigned) 509L else 510L
        mockBehavior(assignedProject = if (isAssigned) projectId else null)
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns projectWithEditCollaborator()
        every { securityService.getUserIdOrThrow() } returns NOT_RELATED_USER

        assertThat(authorization.canEditContractsAndAgreements(projectId)).isFalse()
        assertThat(authorization.canEditProjectManagers(projectId)).isFalse()
        assertThat(authorization.canEditReportingSchedule(projectId)).isFalse()
    }

    @ParameterizedTest(name = "canEdit - collaborator with permission - is assigned {0})")
    @ValueSource(booleans = [true, false])
    fun `canEdit - collaborator with permission`(isAssigned: Boolean) {
        val projectId = if (isAssigned) 511L else 512L
        mockBehavior(assignedProject = if (isAssigned) projectId else null, ProjectCreatorContractingReportingEdit)
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns projectWithEditCollaborator()
        every { securityService.getUserIdOrThrow() } returns COLLABORATOR_EDIT

        assertThat(authorization.canEditContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canEditProjectManagers(projectId)).isTrue()
        assertThat(authorization.canEditReportingSchedule(projectId)).isTrue()
    }

    @ParameterizedTest(name = "canEdit - collaborator without permission - is assigned {0})")
    @ValueSource(booleans = [true, false])
    fun `canEdit - collaborator without permission`(isAssigned: Boolean) {
        val projectId = if (isAssigned) 513L else 514L
        mockBehavior(assignedProject = if (isAssigned) projectId else null)
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns projectWithEditCollaborator()
        every { securityService.getUserIdOrThrow() } returns COLLABORATOR_EDIT

        assertThat(authorization.canEditReportingSchedule(projectId)).isFalse()
    }

    @Test
    fun `canEdit - controller`() {
        val projectId = 515L
        mockBehavior(assignedProject = null, ProjectContractsEdit, ProjectContractingManagementEdit, ProjectContractingReportingEdit)
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns projectWithEditCollaborator()
        every { securityService.getUserIdOrThrow() } returns CONTROLLER

        assertThat(authorization.canEditContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canEditProjectManagers(projectId)).isTrue()
        assertThat(authorization.canEditReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canEdit - monitor user - is assigned`() {
        val projectId = 516L
        mockBehavior(projectId, ProjectContractsEdit, ProjectContractingManagementEdit, ProjectContractingReportingEdit)

        assertThat(authorization.canEditContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canEditProjectManagers(projectId)).isTrue()
        assertThat(authorization.canEditReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canEdit - monitor user - is not assigned - has retrieve`() {
        val projectId = 517L
        mockBehavior(assignedProject = null,
            ProjectContractsEdit, ProjectContractingManagementEdit, ProjectContractingReportingEdit, ProjectRetrieve)

        assertThat(authorization.canEditContractsAndAgreements(projectId)).isTrue()
        assertThat(authorization.canEditProjectManagers(projectId)).isTrue()
        assertThat(authorization.canEditReportingSchedule(projectId)).isTrue()
    }

    @Test
    fun `canEdit - monitor user - is not assigned - has not retrieve`() {
        val projectId = 518L
        mockBehavior(assignedProject = null,
            ProjectContractsEdit, ProjectContractingManagementEdit, ProjectContractingReportingEdit)
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns projectWithEditCollaborator()
        every { securityService.getUserIdOrThrow() } returns NOT_RELATED_USER

        assertThat(authorization.canEditContractsAndAgreements(projectId)).isFalse()
        assertThat(authorization.canEditProjectManagers(projectId)).isFalse()
        assertThat(authorization.canEditReportingSchedule(projectId)).isFalse()
    }

}
