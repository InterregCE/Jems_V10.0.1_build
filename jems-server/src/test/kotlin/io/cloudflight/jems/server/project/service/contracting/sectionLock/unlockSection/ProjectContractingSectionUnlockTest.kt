package io.cloudflight.jems.server.project.service.contracting.sectionLock.unlockSection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import kotlin.random.Random

class ProjectContractingSectionUnlockTest : UnitTest() {

    companion object {
        const val projectId = 1L
        val contractingSection = ProjectContractingSection.values()[Random.nextInt(ProjectContractingSection.values().size)]
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var authorization: Authorization

    @MockK
    lateinit var persistence: ProjectContractingSectionLockPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: ProjectContractingSectionUnlock

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
    }

    @Test
    fun canUnlockProjectContractingSectionAsAdmin() {
        every { securityService.currentUser } returns adminUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns true
        every { persistence.isLocked(projectId, contractingSection) } returns true
        every { persistence.unlock(projectId, contractingSection
        ) } returns Unit

        interactor.unlock(contractingSection, projectId)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACTING_SECTION_UNLOCKED,
                project = AuditProject(id = projectId.toString()),
                description = "Project contracting section ${contractingSection.name} was set to Unlocked"
            )
        )
    }

    @Test
    fun canUnlockProjectContractingSectionAsAUserWithSufficientRights() {
        every { securityService.currentUser } returns programmeUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns true
        every { persistence.isLocked(projectId, contractingSection) } returns true
        every { persistence.unlock(projectId, contractingSection
        ) } returns Unit

        interactor.unlock(contractingSection, projectId)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACTING_SECTION_UNLOCKED,
                project = AuditProject(id = projectId.toString()),
                description = "Project contracting section ${contractingSection.name} was set to Unlocked"
            )
        )
    }

    @Test
    fun cannotUnlockProjectContractingSectionAsAnUnauthorizedUser() {
        val userTypes = listOf(applicantUser, programmeUser)
        val currentUser = userTypes[Random.nextInt(userTypes.size)]
        val exception = ProjectContractingSectionUnlockException(Exception())

        every { securityService.currentUser } returns currentUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns false
        every { persistence.isLocked(projectId, contractingSection) } returns true
        every { persistence.unlock(projectId, contractingSection
        ) } throws exception

        assertThrows<ProjectContractingSectionUnlockException> { interactor.unlock(contractingSection, projectId) }
    }
}
