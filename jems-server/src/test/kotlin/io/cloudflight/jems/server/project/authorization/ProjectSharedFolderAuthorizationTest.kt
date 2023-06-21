package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
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

internal class ProjectSharedFolderAuthorizationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 761L
    }

    @MockK
    private lateinit var securityService: SecurityService

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var authorization: ProjectSharedFolderAuthorization

    @BeforeEach
    fun resetMocks() {
        clearMocks(securityService, projectPersistence, controllerInstitutionPersistence)
    }

    @ParameterizedTest(name = "canRetrieveSharedFolderFile - creatorView: {0}, creatorEdit: {1}, monitorView: {2}, monitorEdit: {3} - result {4}")
    @CsvSource(
        value = [
            // Good cases
            "true,false,false,false,true",
            "false,true,false,false,true",
            "false,false,true,false,true",
            "false,false,false,true,true",
            "true,true,true,true,true",
            // Bad case
            "false,false,false,false,false"
        ]
    )
    fun canRetrieve(creatorView: Boolean, creatorEdit: Boolean, monitorView: Boolean, monitorEdit: Boolean, result: Boolean) {
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns mockk {
            every { getUserIdsWithViewLevel() } returns setOf(USER_ID)
        }
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf(USER_ID)
        every { securityService.getUserIdOrThrow() } returns USER_ID

        mockViewEdit(creatorView, creatorEdit, monitorView, monitorEdit)

        assertEquals(authorization.canRetrieveSharedFolderFile(PROJECT_ID), result)
    }

    @ParameterizedTest(name = "canEditSharedFolderFile - creatorView: {0}, creatorEdit: {1}, monitorView: {2}, monitorEdit: {3} - result {4}")
    @CsvSource(
        value = [
            // Good cases
            "false,true,false,false,true",
            "false,false,false,true,true",
            "true,true,true,true,true",
            // Bad case
            "false,false,true,false,false",
            "true,false,false,false,false",
            "false,false,false,false,false"
        ]
    )
    fun canEditSharedFolderFile(creatorView: Boolean, creatorEdit: Boolean, monitorView: Boolean, monitorEdit: Boolean, result: Boolean) {
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns mockk {
            every { getUserIdsWithEditLevel() } returns setOf(USER_ID)
        }
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf(USER_ID)
        every { securityService.getUserIdOrThrow() } returns USER_ID

        mockViewEdit(creatorView, creatorEdit, monitorView, monitorEdit)

        assertEquals(authorization.canEditSharedFolderFile(PROJECT_ID), result)
    }

    @ParameterizedTest(name = "canDeleteSharedFolderFile - creatorView: {0}, creatorEdit: {1}, monitorView: {2}, monitorEdit: {3} - result {4}")
    @CsvSource(
        value = [
            // Good cases
            "false,false,false,true,true",
            "true,true,true,true,true",
            // Bad case
            "false,true,false,false,false",
            "false,false,true,false,false",
            "true,false,false,false,false",
            "false,false,false,false,false"
        ]
    )
    fun canDeleteSharedFolderFile(creatorView: Boolean, creatorEdit: Boolean, monitorView: Boolean, monitorEdit: Boolean, result: Boolean) {
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns mockk {
            every { getUserIdsWithEditLevel() } returns setOf(USER_ID)
        }
        every { controllerInstitutionPersistence.getRelatedUserIdsForProject(PROJECT_ID) } returns setOf(USER_ID)
        every { securityService.getUserIdOrThrow() } returns USER_ID

        mockViewEdit(creatorView, creatorEdit, monitorView, monitorEdit)

        assertEquals(authorization.canDeleteSharedFolderFile(PROJECT_ID), result)
    }

    private fun mockViewEdit(creatorView: Boolean, creatorEdit: Boolean, monitorView: Boolean, monitorEdit: Boolean) {
        every { securityService.currentUser } returns mockk {
            every { hasPermission(UserRolePermission.ProjectRetrieve) } returns true
            every { hasPermission(UserRolePermission.ProjectCreatorSharedFolderView) } returns (creatorView || creatorEdit)
            every { hasPermission(UserRolePermission.ProjectCreatorSharedFolderEdit) } returns creatorEdit
            every { hasPermission(UserRolePermission.ProjectMonitorSharedFolderView) } returns (monitorView || monitorEdit)
            every { hasPermission(UserRolePermission.ProjectMonitorSharedFolderEdit) } returns monitorEdit
            every { user } returns mockk { every { assignedProjects } returns emptySet() }
        }
    }



}
