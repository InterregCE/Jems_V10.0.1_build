package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerCollaboratorEntity
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectCollaboratorEntity
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.utils.USER_ID
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ProjectAuditControlAuthorizationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 999L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerCollaboratorRepository: UserPartnerCollaboratorRepository

    @MockK
    lateinit var projectCollaboratorRepository: UserProjectCollaboratorRepository

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var authorization: ProjectAuditControlAuthorization

    @BeforeEach
    fun setup() {
        clearMocks(securityService, partnerCollaboratorRepository, projectCollaboratorRepository, controllerInstitutionPersistence)
    }

    @ParameterizedTest(name = "canViewAuditControl - isMonitor: {0}, isPartnerCollaborator: {1}, isProjectCollaborator: {2}, isController: {3} - result {4}")
    @CsvSource(
        value = [
            // Good cases
            "true,false,false,false,true",
            "false,true,false,false,true",
            "false,false,true,false,true",
            "false,false,false,true,true",
            // Bad case
            "false,false,false,false,false",
        ]
    )
    fun canViewAuditControl(isMonitor: Boolean, isPartnerCollaborator: Boolean, isProjectCollaborator: Boolean, isController: Boolean, result: Boolean) {
        val monitorProjects = if (isMonitor) setOf(PROJECT_ID) else setOf()
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { securityService.currentUser } returns mockk {
            every { hasPermission(UserRolePermission.ProjectRetrieve) } returns isMonitor
            every { hasPermission(UserRolePermission.ProjectMonitorAuditAndControlView) } returns true
            every { user.assignedProjects } returns monitorProjects
        }

        val partners = if (isPartnerCollaborator) setOf<UserPartnerCollaboratorEntity>(mockk { every { id.userId } returns USER_ID }) else emptySet()
        val projectCollaborators =
            if (isProjectCollaborator) setOf<UserProjectCollaboratorEntity>(mockk { every { id.userId } returns USER_ID }) else emptySet()
        every { partnerCollaboratorRepository.findAllByProjectId(PROJECT_ID) } returns partners
        every { projectCollaboratorRepository.findAllByIdProjectId(PROJECT_ID) } returns projectCollaborators

        val controllers = if (isController) setOf(USER_ID) else emptySet()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns controllers

        assertEquals(authorization.canViewAuditAndControl(PROJECT_ID), result)
    }

    @ParameterizedTest(name = "canEditAuditControl - isMonitor: {0}, isPartnerCollaborator: {1}, isProjectCollaborator: {2}, isController: {3} - result {4}")
    @CsvSource(
        value = [
            // Good cases
            "true,false,false,false,true",
            "false,false,false,true,true",
            // Bad cases
            "false,true,false,false,false",
            "false,false,true,false,false",
            "false,false,false,false,false",
        ]
    )
    fun canEditAuditControl(isMonitor: Boolean, isPartnerCollaborator: Boolean, isProjectCollaborator: Boolean, isController: Boolean, result: Boolean) {
        val monitorProjects = if (isMonitor) setOf(PROJECT_ID) else setOf()
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { securityService.currentUser } returns mockk {
            every { hasPermission(UserRolePermission.ProjectRetrieve) } returns isMonitor
            every { hasPermission(UserRolePermission.ProjectMonitorAuditAndControlEdit) } returns true
            every { user.assignedProjects } returns monitorProjects
        }

        val controllers = if (isController) setOf(USER_ID) else emptySet()
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns controllers

        assertEquals(authorization.canEditAuditAndControl(PROJECT_ID), result)
    }

}
