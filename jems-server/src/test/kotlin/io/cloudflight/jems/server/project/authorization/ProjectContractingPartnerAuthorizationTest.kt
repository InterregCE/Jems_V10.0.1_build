package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional


internal class ProjectContractingPartnerAuthorizationTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 9L
        private const val PROJECT_CREATOR_USER_ID = 3L

        private const val PROJECT_COLLABORATOR_VIEW_USER_ID = 4L
        private const val PROJECT_COLLABORATOR_EDIT_USER_ID = 5L
        private const val PROJECT_COLLABORATOR_MANAGE_USER_ID = 6L

        private const val PARTNER_COLLABORATOR_USER_ID = 7L

        private const val PARTNER_ID = 10L

        private val applicantAndStatus = ProjectApplicantAndStatus(
            projectId = PROJECT_ID,
            applicantId = PROJECT_CREATOR_USER_ID,
            collaboratorViewIds = setOf(PROJECT_COLLABORATOR_VIEW_USER_ID),
            collaboratorEditIds = setOf(PROJECT_COLLABORATOR_EDIT_USER_ID),
            collaboratorManageIds = setOf(PROJECT_COLLABORATOR_MANAGE_USER_ID),
            projectStatus = ApplicationStatus.CONTRACTED
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var currentUser: CurrentUser

    @MockK
    lateinit var projectAuthorization: ProjectAuthorization

    @InjectMockKs
    lateinit var authorization: ProjectContractingPartnerAuthorization


    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService)
        clearMocks(partnerCollaboratorPersistence)
        every { securityService.currentUser } returns currentUser
    }

    @Test
    fun `partner with edit capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.of(PartnerCollaboratorLevel.EDIT)
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        Assertions.assertThat(authorization.hasEditPermission(PARTNER_ID)).isTrue
        Assertions.assertThat(authorization.hasViewPermission(PARTNER_ID)).isTrue
    }

    @Test
    fun `partner with only view capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.of(PartnerCollaboratorLevel.VIEW)
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        Assertions.assertThat(authorization.hasEditPermission(PARTNER_ID)).isFalse
        Assertions.assertThat(authorization.hasViewPermission(PARTNER_ID)).isTrue
    }

    @Test
    fun `user who is not partner`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns false
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.empty()
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns false
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        Assertions.assertThat(authorization.hasEditPermission(PARTNER_ID)).isFalse
        Assertions.assertThat(authorization.hasViewPermission(PARTNER_ID)).isFalse
    }

    @Test
    fun `programme user with view capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns false
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.empty()
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns false
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        Assertions.assertThat(authorization.hasEditPermission(PARTNER_ID)).isFalse
        Assertions.assertThat(authorization.hasViewPermission(PARTNER_ID)).isTrue
    }

    @Test
    fun `programme user with edit capability`() {
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerEdit) } returns true
        every { currentUser.hasPermission(UserRolePermission.ProjectContractingPartnerView) } returns true
        every { securityService.getUserIdOrThrow() } returns PARTNER_COLLABORATOR_USER_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID)} returns applicantAndStatus
        every { partnerCollaboratorPersistence.findByUserIdAndPartnerId(any(), any()) } returns Optional.empty()
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, any()) } returns true
        every { projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, any()) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        Assertions.assertThat(authorization.hasEditPermission(PARTNER_ID)).isTrue
        Assertions.assertThat(authorization.hasViewPermission(PARTNER_ID)).isTrue
    }
}
