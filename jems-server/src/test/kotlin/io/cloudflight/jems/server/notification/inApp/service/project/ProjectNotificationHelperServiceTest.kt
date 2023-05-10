package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserEmailNotification
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.cloudflight.jems.server.utils.PARTNER_ID
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class ProjectNotificationHelperServiceTest {

    companion object {
        private const val PROJECT_ID = 5L

        val projectNotificationConfigAll = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmittedStep1,
            active = true,
            sendToManager = true,
            sendToLeadPartner = true,
            sendToProjectPartners = true,
            sendToProjectAssigned = true,
            sendToControllers = false,
            emailSubject = "Application Step 1 Submitted",
            emailBody = "test step 1"
        )

        val projectNotificationConfigNone = ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmitted,
            active = true,
            sendToManager = false,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = false,
            emailSubject = "Application Submitted",
            emailBody = "test"
        )

        val partnerReportNotificationConfig = ProjectNotificationConfiguration(
            id = NotificationType.PartnerReportSubmitted,
            active = true,
            sendToManager = false,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = true,
            emailSubject = "PartnerReport Submitted",
            emailBody = "test"
        )


        val projectManager = CollaboratorAssignedToProject(
            userId = 21L,
            userEmail = "project.manager@jems.eu",
            sendNotificationsToEmail = true,
            userStatus = UserStatus.ACTIVE,
            level = ProjectCollaboratorLevel.MANAGE
        )

        val projectManagerDeactivated = CollaboratorAssignedToProject(
            userId = 211L,
            userEmail = "project.manager.deactivated@jems.eu",
            sendNotificationsToEmail = true,
            userStatus = UserStatus.UNCONFIRMED,
            level = ProjectCollaboratorLevel.MANAGE
        )

        private val leadPartner = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = 2L,
            active = true,
            abbreviation = "A",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = "A",
            nameInEnglish = "A",
            createdAt = ZonedDateTime.now().minusDays(5),
            sortNumber = 1,
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            otherIdentifierDescription = emptySet(),
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = null,
        )

        private val partner = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = 3L,
            active = true,
            abbreviation = "B",
            role = ProjectPartnerRole.PARTNER,
            nameInOriginalLanguage = "B",
            nameInEnglish = "B",
            createdAt = ZonedDateTime.now().minusDays(4),
            sortNumber = 1,
            partnerType = ProjectTargetGroup.Egtc,
            partnerSubType = PartnerSubType.SMALL_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            otherIdentifierDescription = emptySet(),
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = null,
        )

        val leadPartnerCollaborator = PartnerCollaborator(
            userId = 41L,
            partnerId = 2L,
            userEmail = "lp.collaborator@jems.eu",
            sendNotificationsToEmail = true,
            userStatus = UserStatus.ACTIVE,
            level = PartnerCollaboratorLevel.EDIT,
            gdpr = false
        )

        val partnerCollaborator = PartnerCollaborator(
            userId = 43L,
            partnerId = 3L,
            userEmail = "pp.collaborator@jems.eu",
            sendNotificationsToEmail = true,
            userStatus = UserStatus.ACTIVE,
            level = PartnerCollaboratorLevel.EDIT,
            gdpr = false
        )

        val partnerCollaboratorActiveButFlagFalse = PartnerCollaborator(
            userId = 43L,
            partnerId = 3L,
            userEmail = "pp.collaborator.noNotifications@jems.eu",
            sendNotificationsToEmail = false,
            userStatus = UserStatus.ACTIVE,
            level = PartnerCollaboratorLevel.EDIT,
            gdpr = false
        )

        val partnerCollaboratorDeactivated = PartnerCollaborator(
            userId = 431L,
            partnerId = 31L,
            userEmail = "pp.collaborator.deactivated@jems.eu",
            sendNotificationsToEmail = true,
            userStatus = UserStatus.INACTIVE,
            level = PartnerCollaboratorLevel.EDIT,
            gdpr = false
        )

        val programmeUser = UserSummary(
            id = 101L,
            email = "programme@user",
            sendNotificationsToEmail = true,
            name = "john",
            surname = "doe",
            userRole = UserRoleSummary(id = 2, name = "programme user", isDefault = true),
            userStatus = UserStatus.ACTIVE
        )

        val programmeUserDeactivated = UserSummary(
            id = 102L,
            email = "john.doe.deactivated@ce.eu",
            sendNotificationsToEmail = true,
            name = "john",
            surname = "deactivated",
            userRole = UserRoleSummary(id = 2, name = "programme user", isDefault = true),
            userStatus = UserStatus.INACTIVE
        )

        val programmeUserGlobal = UserSummary(
            id = 118L,
            email = "global.retrieve@programme.user",
            sendNotificationsToEmail = true,
            name = "bruno",
            surname = "mars",
            userRole = UserRoleSummary(id = 1151L, name = "aaaadmin", isDefault = false),
            userStatus = UserStatus.ACTIVE,
        )

        val controllerUser = UserSummary(
            id = 11L,
            email = "controller.edit@programme.user",
            sendNotificationsToEmail = true,
            name = "controller",
            surname = "user",
            userRole = UserRoleSummary(id = 3, name = "controller", isDefault = false),
            userStatus = UserStatus.ACTIVE,
        )
    }

    @MockK
    private lateinit var userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    private lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var userProjectPersistence: UserProjectPersistence

    @MockK
    private lateinit var userPersistence: UserPersistence

    @MockK
    private lateinit var userRolePersistence: UserRolePersistence

    @MockK
    private lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    private lateinit var projectNotificationHelper: ProjectNotificationRecipientService

    @Test
    fun getEmailsForProjectNotificationAllActive() {
        every { userProjectCollaboratorPersistence.getUserIdsForProject(PROJECT_ID) } returns listOf(projectManager, projectManagerDeactivated)
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(leadPartner, partner)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(2L)) } returns setOf(leadPartnerCollaborator)
        every { partnerCollaboratorPersistence.findByProjectAndPartners(PROJECT_ID, setOf(3L)) } returns setOf(
            partnerCollaborator,
            partnerCollaboratorDeactivated,
            partnerCollaboratorActiveButFlagFalse
        )
        every { userProjectPersistence.getUsersForProject(PROJECT_ID) } returns setOf(programmeUser, programmeUserDeactivated)

        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getGlobalProjectRetrievePermissions(), emptySet()
            )
        } returns setOf(1151L)
        every { userPersistence.findAllWithRoleIdIn(setOf(1151L)) } returns listOf(programmeUserGlobal)

        val result = projectNotificationHelper.getEmailsForProjectNotification(projectNotificationConfigAll, PROJECT_ID)
        assertThat(result).isEqualTo(
            mapOf(
                programmeUserGlobal.email to UserEmailNotification(true, UserStatus.ACTIVE),
                programmeUserDeactivated.email to UserEmailNotification(true, UserStatus.INACTIVE),
                programmeUser.email to UserEmailNotification(true, UserStatus.ACTIVE),
                leadPartnerCollaborator.userEmail to UserEmailNotification(true, UserStatus.ACTIVE),
                partnerCollaborator.userEmail to UserEmailNotification(true, UserStatus.ACTIVE),
                partnerCollaboratorDeactivated.userEmail to UserEmailNotification(true, UserStatus.INACTIVE),
                partnerCollaboratorActiveButFlagFalse.userEmail to UserEmailNotification(false, UserStatus.ACTIVE),
                projectManager.userEmail to UserEmailNotification(true, UserStatus.ACTIVE),
                projectManagerDeactivated.userEmail to UserEmailNotification(true, UserStatus.UNCONFIRMED),
            )
        )
    }

    @Test
    fun getEmailsForProjectNotificationNoneActive() {
        val result = projectNotificationHelper.getEmailsForProjectNotification(projectNotificationConfigNone, PROJECT_ID)
        assertThat(result).isEqualTo(emptyMap<String, UserEmailNotification>())
    }

    @Test
    fun getEmailsForPartnerReportNotification() {
        every { controllerInstitutionPersistence.getRelatedUserIdsForPartner(PARTNER_ID) } returns setOf(controllerUser.id)
        every { userPersistence.findAllByIds(setOf(controllerUser.id)) } returns listOf(controllerUser)

        val result = projectNotificationHelper.getEmailsForPartnerNotification(partnerReportNotificationConfig, PARTNER_ID)
        assertThat(result).isEqualTo(mapOf(
            controllerUser.email to UserEmailNotification(true, UserStatus.ACTIVE)
        ))
    }
}
