package io.cloudflight.jems.server.project.service.report.project.verification.notification.sendProjectReportVerificationNotification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.notification.handler.ProjectReportDoneByJs
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification.ReportVerificationNotStartedException
import io.cloudflight.jems.server.project.service.report.project.verification.notification.ProjectReportVerificationNotificationPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification.SendVerificationDoneByJsNotification
import io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification.VerificationNotificationNotEnabledInCallException
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate
import java.time.ZonedDateTime

class SendProjectReportVerificationNotificationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 10L
        private const val REPORT_ID = 11L
        private const val CALL_ID = 12L
        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val createdAt = ZonedDateTime.now()

        private fun getProjectReportModel(status: ProjectReportStatus) = ProjectReportModel(
            id = REPORT_ID,
            reportNumber = 1,
            status = status,
            linkedFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadlineId = null,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            projectId = PROJECT_ID,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",
            spfPartnerId = null,

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
            lastReSubmission = mockk(),
            verificationDate = null,
            verificationEndDate = null,
            amountRequested = null,
            totalEligibleAfterVerification = null,
            lastVerificationReOpening = mockk(),
            riskBasedVerification = false,
            riskBasedVerificationDescription = "RISK BASED DESCRIPTION"
        )

        val user = UserSimple(
            id = 3L, name = "applicant",
            surname = "test surname",
            email = "user@applicant.dev",
        )

        val verificationNotification = ProjectReportVerificationNotification(
            id = 1L,
            reportId = REPORT_ID,
            triggeredByUser = user,
            createdAt = createdAt
        )

        val projectReportVerificationDone = ProjectNotificationConfiguration(
            id = NotificationType.ProjectReportVerificationDoneNotificationSent,
            active = true,
            sendToManager = true,
            sendToLeadPartner = true,
            sendToProjectPartners = true,
            sendToProjectAssigned = true,
            sendToControllers = false,
            emailSubject = "Project report verification done",
            emailBody = "test"
        )

        private val currentUser = LocalCurrentUser(
            AuthorizationUtil.userApplicant, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.CallRetrieve.key))
        )

        private val expectedAuditProject =
            AuditProject(id = PROJECT_ID.toString(), customIdentifier = "projectIdentifier", name = "projectAcronym")
    }

    @MockK
    lateinit var persistence: ProjectReportVerificationNotificationPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    lateinit var callNotificationConfigurationsPersistence: CallNotificationConfigurationsPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var sendVerificationDoneByJsNotification: SendVerificationDoneByJsNotification

    @Test
    fun `sendProjectReportVerificationNotification - all validation passed`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns getProjectReportModel(
            ProjectReportStatus.InVerification
        )
        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigurationsPersistence
                .getActiveNotificationOfType(
                    CALL_ID,
                    NotificationType.ProjectReportVerificationDoneNotificationSent
                )
        } returns projectReportVerificationDone
        every { securityService.currentUser } returns currentUser
        every {
            persistence.storeVerificationNotificationMetaData(
                REPORT_ID,
                currentUser.user.id,
                any()
            )
        } returns verificationNotification

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(ProjectReportDoneByJs::class)) } returns Unit

        assertThat(
            sendVerificationDoneByJsNotification.sendVerificationDoneByJsNotification(
                PROJECT_ID, REPORT_ID
            )
        ).isEqualTo(
            verificationNotification
        )

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_REPORT_VERIFICATION_BY_JS_DONE_NOTIFICATION_SENT,
                entityRelatedId = REPORT_ID,
                project = expectedAuditProject,
                description = "[projectIdentifier] Project report R.1 verification by JS is done"
            )
        )
    }

    @Test
    fun `sendProjectReportVerificationNotification - wrong status`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns getProjectReportModel(
            ProjectReportStatus.Finalized
        )
        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigurationsPersistence
                .getActiveNotificationOfType(
                    CALL_ID,
                    NotificationType.ProjectReportVerificationDoneNotificationSent
                )
        } returns projectReportVerificationDone
        every { securityService.currentUser } returns currentUser
        every {
            persistence.storeVerificationNotificationMetaData(
                REPORT_ID,
                currentUser.user.id,
                any()
            )
        } returns verificationNotification

        assertThrows<ReportVerificationNotStartedException> {
            sendVerificationDoneByJsNotification.sendVerificationDoneByJsNotification(
                PROJECT_ID, REPORT_ID
            )
        }
    }

    @Test
    fun `sendProjectReportVerificationNotification - notification not enabled in call`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns getProjectReportModel(
            ProjectReportStatus.InVerification
        )
        every { projectPersistence.getCallIdOfProject(PROJECT_ID) } returns CALL_ID
        every {
            callNotificationConfigurationsPersistence
                .getActiveNotificationOfType(
                    CALL_ID,
                    NotificationType.ProjectReportVerificationDoneNotificationSent
                )
        } returns null
        every { securityService.currentUser } returns currentUser
        every {
            persistence.storeVerificationNotificationMetaData(
                REPORT_ID,
                currentUser.user.id,
                any()
            )
        } returns verificationNotification

        assertThrows<VerificationNotificationNotEnabledInCallException> {
            sendVerificationDoneByJsNotification.sendVerificationDoneByJsNotification(
                PROJECT_ID, REPORT_ID
            )
        }
    }
}
