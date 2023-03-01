package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.unlockPartner

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
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.utils.partner.projectPartnerDetail
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

class ContractingPartnerUnlockTest : UnitTest() {

    companion object {
        const val projectId = 1L
        const val partnerId = 2L
        val partner = projectPartnerDetail(id = partnerId)
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var authorization: Authorization

    @MockK
    lateinit var persistence: ContractingPartnerLockPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: ContractingPartnerUnlock

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
    }

    @Test
    fun canUnlockContractingPartnerAsAdmin() {
        every { securityService.currentUser } returns adminUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns true
        every { persistence.isLocked(partner.id) } returns true
        every { partnerPersistence.getById(partnerId) } returns partner
        every { persistence.unlock(partner.id) } returns Unit

        interactor.unlockPartner(partnerId, projectId)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACTING_SECTION_UNLOCKED,
                project = AuditProject(id = projectId.toString()),
                description = "Project contracting partner LP0 was set to Unlocked"
            )
        )
    }

    @Test
    fun canUnlockContractingPartnerAsAUserWithSufficientRights() {
        every { securityService.currentUser } returns programmeUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns true
        every { persistence.isLocked(partner.id) } returns true
        every { partnerPersistence.getById(partnerId) } returns partner
        every { persistence.unlock(partner.id) } returns Unit

        interactor.unlockPartner(partnerId, projectId)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACTING_SECTION_UNLOCKED,
                project = AuditProject(id = projectId.toString()),
                description = "Project contracting partner LP0 was set to Unlocked"
            )
        )
    }

    @Test
    fun cannotUnlockContractingPartnerAsAnUnauthorizedUser() {
        val userTypes = listOf(applicantUser, programmeUser)
        val currentUser = userTypes[Random.nextInt(userTypes.size)]
        val exception = ContractingPartnerUnlockException(Exception())

        every { securityService.currentUser } returns currentUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns false
        every { persistence.isLocked(partner.id) } returns true
        every { persistence.unlock(partner.id) } throws exception

        assertThrows<ContractingPartnerUnlockException> { interactor.unlockPartner(partnerId, projectId) }
    }

    @Test
    fun cannotUnlockContractingPartnerForANonExistentProject() {
        val invalidProjectId = -1L
        val userTypes = listOf(applicantUser, programmeUser)
        val currentUser = userTypes[Random.nextInt(userTypes.size)]
        val exception = ContractingPartnerUnlockException(Exception())

        every { securityService.currentUser } returns currentUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, invalidProjectId) } returns true
        every { persistence.isLocked(partner.id) } throws exception

        assertThrows<ContractingPartnerUnlockException> { interactor.unlockPartner(partnerId, invalidProjectId) }
    }

    @Test
    fun cannotUnlockContractingPartnerForANonExistentPartner() {
        val invalidPartnerId = -2L
        val userTypes = listOf(applicantUser, programmeUser)
        val currentUser = userTypes[Random.nextInt(userTypes.size)]
        val exception = ContractingPartnerUnlockException(Exception())

        every { securityService.currentUser } returns currentUser
        every { authorization.hasPermissionForProject(UserRolePermission.ProjectSetToContracted, projectId) } returns true
        every { persistence.isLocked(invalidPartnerId) } returns true
        every { persistence.unlock(invalidPartnerId) } throws exception

        assertThrows<ContractingPartnerUnlockException> { interactor.unlockPartner(invalidPartnerId, projectId) }
    }
}
