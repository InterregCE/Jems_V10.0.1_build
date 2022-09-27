package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingView
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class ProjectReportAuthorizationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 288L
        private const val PROJECT_ID = 305L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @MockK
    lateinit var currentUser: CurrentUser

    @InjectMockKs
    lateinit var reportAuthorization: ProjectReportAuthorization

    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, null) } returns PROJECT_ID
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService)
        every { securityService.currentUser } returns currentUser
    }

    @Test
    fun `assigned monitor user with permission can edit not specific`() {
        every { currentUser.hasPermission(ProjectReportingEdit) } returns true
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        assertThat(reportAuthorization.canEditPartnerReportNotSpecific(PARTNER_ID)).isTrue
    }

    @Test
    fun `creator + collaborator with permission can edit not specific`() {
        every { currentUser.hasPermission(ProjectReportingEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns 4590L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 4590L, PARTNER_ID) } returns Optional.of(EDIT)
        assertThat(reportAuthorization.canEditPartnerReportNotSpecific(PARTNER_ID)).isTrue
    }

    @Test
    fun `user can NOT edit not specific`() {
        every { currentUser.hasPermission(ProjectReportingEdit) } returns false
        every { securityService.getUserIdOrThrow() } returns 3205L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 3205L, PARTNER_ID) } returns Optional.empty()
        assertThat(reportAuthorization.canEditPartnerReportNotSpecific(PARTNER_ID)).isFalse
    }

    @ParameterizedTest(name = "assigned monitor user with permission can edit (isOpen {0})")
    @ValueSource(booleans = [true, false])
    fun `assigned monitor user with permission can edit`(isOpen: Boolean) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status.isOpen() } returns isOpen
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 17L) } returns report

        every { currentUser.hasPermission(ProjectReportingEdit) } returns true
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        assertThat(reportAuthorization.canEditPartnerReport(PARTNER_ID, 17L)).isEqualTo(isOpen)
    }

    @Test
    fun `assigned monitor user with permission can view`() {
        every { currentUser.hasPermission(ProjectReportingView) } returns true
        every { currentUser.user.assignedProjects } returns setOf(PROJECT_ID)
        assertThat(reportAuthorization.canViewPartnerReport(PARTNER_ID)).isTrue
    }

    @Test
    fun `creator + collaborator with permission can view`() {
        every { currentUser.hasPermission(ProjectReportingView) } returns false
        every { securityService.getUserIdOrThrow() } returns 4590L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 4590L, PARTNER_ID) } returns Optional.of(VIEW)
        assertThat(reportAuthorization.canViewPartnerReport(PARTNER_ID)).isTrue
    }

    @Test
    fun `user can NOT view`() {
        every { currentUser.hasPermission(ProjectReportingView) } returns false
        every { securityService.getUserIdOrThrow() } returns 3205L
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = 3205L, PARTNER_ID) } returns Optional.empty()
        assertThat(reportAuthorization.canViewPartnerReport(PARTNER_ID)).isFalse
    }

    @ParameterizedTest(name = "control report - user can view (status {0})")
    @EnumSource(value = UserInstitutionAccessLevel::class)
    fun `control report - user can view`(accessLevel: UserInstitutionAccessLevel) {
        val userId = 10L + accessLevel.ordinal
        every { securityService.getUserIdOrThrow() } returns userId
        every { controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(userId, partnerId = 50L) } returns accessLevel
        assertThat(reportAuthorization.canViewPartnerControlReport(50L)).isTrue()
    }

    @Test
    fun `control report - user can not view`() {
        val userId = 18L
        every { securityService.getUserIdOrThrow() } returns userId
        every { controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(userId, partnerId = -1L) } returns null
        assertThat(reportAuthorization.canViewPartnerControlReport(-1L)).isFalse()
    }

    @Test
    fun `control report - user can edit`() {
        val userId = 26L
        every { securityService.getUserIdOrThrow() } returns userId
        every { controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(userId, partnerId = 28L) } returns
            UserInstitutionAccessLevel.Edit
        assertThat(reportAuthorization.canEditPartnerControlReport(28L)).isTrue()
    }

    @Test
    fun `control report - user can not edit - only view`() {
        val userId = 28L
        every { securityService.getUserIdOrThrow() } returns userId
        every { controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(userId, partnerId = 150L) } returns
            UserInstitutionAccessLevel.View
        assertThat(reportAuthorization.canEditPartnerControlReport(150L)).isFalse()
    }

    @Test
    fun `control report - user can not edit - not assigned`() {
        val userId = 30L
        every { securityService.getUserIdOrThrow() } returns userId
        every { controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(userId, partnerId = 152L) } returns null
        assertThat(reportAuthorization.canEditPartnerControlReport(152L)).isFalse()
    }

    @Test
    fun `canUpdatePartner - has EDIT`() {
        val partnerId = 1198L
        val userId = 458L

        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = userId, partnerId = partnerId) } returns Optional.of(EDIT)

        assertThat(reportAuthorization.canUpdatePartner(partnerId)).isTrue()
    }

    @Test
    fun `canUpdatePartner - has only VIEW`() {
        val partnerId = 1288L
        val userId = 456L

        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = userId, partnerId = partnerId) } returns Optional.of(VIEW)

        assertThat(reportAuthorization.canUpdatePartner(partnerId)).isFalse()
    }

    @Test
    fun `canUpdatePartner - has no partner-related permission`() {
        val partnerId = 1279L
        val userId = 468L

        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId = userId, partnerId = partnerId) } returns Optional.empty()

        assertThat(reportAuthorization.canUpdatePartner(partnerId)).isFalse()
    }

    @ParameterizedTest(name = "canRetrievePartner - is Partner collaborator (level {0})")
    @EnumSource(value = PartnerCollaboratorLevel::class)
    fun `canRetrievePartner - is Partner collaborator`(level: PartnerCollaboratorLevel) {
        val partnerId = 1192L
        val userId = 546L
        val projectId = 452L
        val project = mockk<ProjectApplicantAndStatus>()
        every { project.projectId } returns projectId
        every { project.getUserIdsWithViewLevel() } returns emptySet()
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns project

        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId, partnerId = partnerId) } returns Optional.of(level)

        assertThat(reportAuthorization.canRetrievePartner(partnerId)).isTrue()
    }

    @Test
    fun `canRetrievePartner - is in institution`() {
        val partnerId = 1193L
        val userId = 547L
        val projectId = 453L
        val project = mockk<ProjectApplicantAndStatus>()
        every { project.projectId } returns projectId
        every { project.getUserIdsWithViewLevel() } returns setOf(userId)
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns project

        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId, partnerId = partnerId) } returns Optional.empty()

        assertThat(reportAuthorization.canRetrievePartner(partnerId)).isTrue()
    }

    @Test
    fun `canRetrievePartner - cannot retrieve`() {
        val partnerId = 1194L
        val userId = 548L
        val projectId = 454L
        val project = mockk<ProjectApplicantAndStatus>()
        every { project.projectId } returns projectId
        every { project.getUserIdsWithViewLevel() } returns emptySet()
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { projectPersistence.getApplicantAndStatusById(projectId) } returns project

        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(userId, partnerId = partnerId) } returns Optional.empty()

        assertThat(reportAuthorization.canRetrievePartner(partnerId)).isFalse()
    }

}
