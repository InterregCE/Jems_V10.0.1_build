package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class AssignUserEventListenersTest : UnitTest() {

    companion object {
        val project = ProjectSummary(1L, "cid", "call", "acronym", ApplicationStatus.STEP1_DRAFT)
        val userRole = UserRoleSummary(2L, "role", false)
        val user = UserSummary(3L, "email", "", "", userRole, UserStatus.ACTIVE)
        val otherUser = UserSummary(4L, "other@email", "", "", userRole, UserStatus.ACTIVE)
    }

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var AppProperties: AppProperties

    @InjectMockKs
    lateinit var assignUserEventListeners: AssignUserEventListeners

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `assigning a user triggers an audit log`() {
        val auditSlot = slot<JemsAuditEvent>()
        val assignUserEvent = AssignUserEvent(AssignUserToProjectTest.project, listOf(user, otherUser))

        assignUserEventListeners.publishJemsAuditEvent(assignUserEvent)

        verify { eventPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_USER_ASSIGNMENT,
                project = AuditProject(project.id.toString(), project.customIdentifier, project.acronym),
                description = "Users can access: ${user.email}, ${otherUser.email}"
            )
        )
    }

    @Test
    fun `removing assignments triggers an audit log`() {
        val auditSlot = slot<JemsAuditEvent>()
        val assignUserEvent = AssignUserEvent(AssignUserToProjectTest.project, emptyList())

        assignUserEventListeners.publishJemsAuditEvent(assignUserEvent)

        verify { eventPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_USER_ASSIGNMENT,
                project = AuditProject(project.id.toString(), project.customIdentifier, project.acronym),
                description = "Users can access: "
            )
        )
    }
}
