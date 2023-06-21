package io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class AssignUserCollaboratorToProjectEventListenersTest : UnitTest() {

    companion object {
        val project = ProjectSummary(1L, "cid", 1L, "call", "acronym", ApplicationStatus.STEP1_DRAFT)
        val user = CollaboratorAssignedToProject(3L, "email", sendNotificationsToEmail = false, UserStatus.ACTIVE, ProjectCollaboratorLevel.EDIT)
        val otherUser = CollaboratorAssignedToProject(4L, "other@email", sendNotificationsToEmail = false, UserStatus.ACTIVE, ProjectCollaboratorLevel.VIEW)
    }

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var assignUserCollaboratorToProjectEventListeners: AssignUserCollaboratorToProjectEventListeners

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `assigning a collaborator triggers an audit log`() {
        val auditSlot = slot<JemsAuditEvent>()
        val assignUserCollaboratorToProjectEvent = AssignUserCollaboratorToProjectEvent(project, listOf(user, otherUser))

        assignUserCollaboratorToProjectEventListeners.publishJemsAuditEvent(assignUserCollaboratorToProjectEvent)

        verify { eventPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_USER_ASSIGNMENT_APPLICANTS,
                project = AuditProject(project.id.toString(), project.customIdentifier, project.acronym),
                description = "[Applicant form users] List of users:: [${user.userEmail}: EDIT, ${otherUser.userEmail}: VIEW]"
            )
        )
    }

    @Test
    fun `removing collaborator triggers an audit log`() {
        val auditSlot = slot<JemsAuditEvent>()
        val assignUserCollaboratorToProjectEvent = AssignUserCollaboratorToProjectEvent(project, emptyList())

        assignUserCollaboratorToProjectEventListeners.publishJemsAuditEvent(assignUserCollaboratorToProjectEvent)

        verify { eventPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_USER_ASSIGNMENT_APPLICANTS,
                project = AuditProject(project.id.toString(), project.customIdentifier, project.acronym),
                description = "[Applicant form users] List of users:: []"
            )
        )
    }
}
