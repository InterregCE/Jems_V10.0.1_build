package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.context.ApplicationEventPublisher
import java.lang.RuntimeException
import java.util.stream.Stream

class ProjectNotificationEventListenerTest : UnitTest() {

    companion object {
        private const val CALL_ID = 1L
        private const val PROJECT_ID = 5L

        private fun summary(status: ApplicationStatus) = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = CALL_ID,
            callName = "call",
            acronym = "project acronym",
            status = status,
        )

        @JvmStatic
        fun parameterizedTestValues(): Stream<Arguments> = Stream.of(
            Arguments.of(ApplicationStatus.DRAFT, ApplicationStatus.SUBMITTED, NotificationType.ProjectSubmitted),
            Arguments.of(ApplicationStatus.DRAFT, ApplicationStatus.STEP1_SUBMITTED, NotificationType.ProjectSubmittedStep1),
            Arguments.of(ApplicationStatus.STEP1_SUBMITTED, ApplicationStatus.STEP1_APPROVED, NotificationType.ProjectApprovedStep1),
            Arguments.of(ApplicationStatus.STEP1_SUBMITTED, ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS, NotificationType.ProjectApprovedWithConditionsStep1),
            Arguments.of(ApplicationStatus.STEP1_SUBMITTED, ApplicationStatus.STEP1_INELIGIBLE, NotificationType.ProjectIneligibleStep1),
            Arguments.of(ApplicationStatus.STEP1_SUBMITTED, ApplicationStatus.STEP1_NOT_APPROVED, NotificationType.ProjectNotApprovedStep1),
            Arguments.of(ApplicationStatus.ELIGIBLE, ApplicationStatus.APPROVED, NotificationType.ProjectApproved),
            Arguments.of(ApplicationStatus.ELIGIBLE, ApplicationStatus.APPROVED_WITH_CONDITIONS, NotificationType.ProjectApprovedWithConditions),
            Arguments.of(ApplicationStatus.ELIGIBLE, ApplicationStatus.INELIGIBLE, NotificationType.ProjectIneligible),
            Arguments.of(ApplicationStatus.ELIGIBLE, ApplicationStatus.NOT_APPROVED, NotificationType.ProjectNotApproved),
            Arguments.of(ApplicationStatus.ELIGIBLE, ApplicationStatus.RETURNED_TO_APPLICANT, NotificationType.ProjectReturnedToApplicant),
            Arguments.of(ApplicationStatus.RETURNED_TO_APPLICANT, ApplicationStatus.SUBMITTED, NotificationType.ProjectResubmitted),
            Arguments.of(ApplicationStatus.ELIGIBLE, ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS, NotificationType.ProjectReturnedForConditions),
            Arguments.of(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS, ApplicationStatus.CONDITIONS_SUBMITTED, NotificationType.ProjectConditionsSubmitted),
            Arguments.of(ApplicationStatus.APPROVED, ApplicationStatus.CONTRACTED, NotificationType.ProjectContracted),
            Arguments.of(ApplicationStatus.CONTRACTED, ApplicationStatus.IN_MODIFICATION, NotificationType.ProjectInModification),
            Arguments.of(ApplicationStatus.APPROVED, ApplicationStatus.MODIFICATION_PRECONTRACTING, NotificationType.ProjectInModification),
            Arguments.of(ApplicationStatus.IN_MODIFICATION, ApplicationStatus.MODIFICATION_SUBMITTED, NotificationType.ProjectModificationSubmitted),
            Arguments.of(ApplicationStatus.CONDITIONS_SUBMITTED, ApplicationStatus.APPROVED, NotificationType.ProjectApproved),
            Arguments.of(ApplicationStatus.MODIFICATION_PRECONTRACTING, ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED, NotificationType.ProjectModificationSubmitted),
            Arguments.of(ApplicationStatus.MODIFICATION_SUBMITTED, ApplicationStatus.CONTRACTED, NotificationType.ProjectModificationApproved),
            Arguments.of(ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED, ApplicationStatus.APPROVED, NotificationType.ProjectModificationApproved),
            Arguments.of(ApplicationStatus.MODIFICATION_SUBMITTED, ApplicationStatus.MODIFICATION_REJECTED, NotificationType.ProjectModificationRejected),
            Arguments.of(ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED, ApplicationStatus.MODIFICATION_REJECTED, NotificationType.ProjectModificationRejected),
        )!!
    }

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var notificationProjectService: GlobalProjectNotificationServiceInteractor

    @InjectMockKs
    lateinit var listener: ProjectNotificationEventListener

    @BeforeEach
    internal fun reset() {
        clearAllMocks()
    }

    @ParameterizedTest
    @MethodSource("parameterizedTestValues")
    fun `send notification`(currentStatus: ApplicationStatus, newStatus: ApplicationStatus, notificationType: NotificationType) {
        val slotVariable = slot<Map<NotificationVariable, Any>>()
        every { notificationProjectService.sendNotifications(any(), capture(slotVariable)) } answers { }

        listener.sendNotifications(
            ProjectStatusChangeEvent(mockk(), summary(currentStatus), newStatus)
        )

        verify(exactly = 1) { notificationProjectService.sendNotifications(notificationType, any()) }
        assertThat(slotVariable.captured).contains(
            entry(NotificationVariable.ProjectId, PROJECT_ID),
            entry(NotificationVariable.ProjectIdentifier, "01"),
            entry(NotificationVariable.ProjectAcronym, "project acronym"),
        )
    }

    @Test
    fun storeAudit() {
        val slotAudit = slot<JemsAuditEvent>()
        every { eventPublisher.publishEvent(capture(slotAudit)) } answers { }

        val applicationStatus = ApplicationStatus.CONDITIONS_SUBMITTED
        val applicationEvent = ProjectStatusChangeEvent(
            context = this,
            projectSummary = summary(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS),
            newStatus = applicationStatus,
        )

        listener.storeAudit(applicationEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(any<Any>()) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = "5", customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from RETURNED_TO_APPLICANT_FOR_CONDITIONS to CONDITIONS_SUBMITTED"
            )
        )
    }

}
