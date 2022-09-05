package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.approvedAndAfterApprovedApplicationStatuses
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.updateInstitutionUsersProjectAssignment.UpdateInstitutionUsersProjectAssignment
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class AssignInstitutionToPartnerTest: UnitTest() {

    companion object {
        private const val INSTITUTION_ID = 1L

        private val institutionAssignment = InstitutionPartnerAssignment(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerProjectId = 0L
        )
    }

    @RelaxedMockK
    lateinit var  controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var partnerPersistence: PartnerPersistenceProvider

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var updateInstitutionUsersProjectAssignment: UpdateInstitutionUsersProjectAssignment

    @InjectMockKs
    lateinit var assignInstitutionToPartner: AssignInstitutionToPartner


    @BeforeEach
    fun resetMocks() {
        clearMocks(controllerInstitutionPersistence)
        clearMocks(partnerPersistence)
    }

    @Test
    fun `assign institution to partner`() {
        val newAssignment = ControllerInstitutionAssignment(
            assignmentsToAdd = listOf(institutionAssignment),
            assignmentsToRemove = emptyList()
        )
        val assignmentsPartnerIds = newAssignment.assignmentsToAdd.map { it.partnerId }.union(newAssignment.assignmentsToRemove.map { it.partnerId })
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByPartnerIdsIn(assignmentsPartnerIds) } returns emptyList()

        every { partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
            assignmentsPartnerIds, approvedAndAfterApprovedApplicationStatuses) } returns listOf(Pair(1L, 1L))

       every { controllerInstitutionPersistence.assignInstitutionToPartner(
           assignmentsToRemove = emptyList(),
           assignmentsToSave = listOf(InstitutionPartnerAssignment(
               institutionId = 1L,
               partnerId = 1L,
               partnerProjectId = 1L
           ))
       ) }  returns listOf(InstitutionPartnerAssignment(institutionId = 1L, partnerId = 1L, partnerProjectId = 1L))

        every { updateInstitutionUsersProjectAssignment.updateInstitutionUsersProjectAssignment(any(), any(), any()) } just Runs

        assertThat(assignInstitutionToPartner.assignInstitutionToPartner(newAssignment)).containsExactly(
            InstitutionPartnerAssignment(
                institutionId = 1L,
                partnerId = 1L,
                partnerProjectId = 1L
            )
        )

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        with(slotAudit.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_CHANGED)
            assertThat(description).startsWith(
                "Assignment of institution to partner changed to"
            )
        }
    }

    @Test
    fun `assign institution to partner - throws partner not valid`() {
        val institutionAssignment = InstitutionPartnerAssignment(
            institutionId = INSTITUTION_ID,
            partnerId = 9L,
            partnerProjectId = 0L
        )
        val newAssignment = ControllerInstitutionAssignment(
            assignmentsToAdd = listOf(institutionAssignment),
            assignmentsToRemove = emptyList()
        )
        val assignmentsPartnerIds = newAssignment.assignmentsToAdd.map { it.partnerId }.union(newAssignment.assignmentsToRemove.map { it.partnerId })
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByPartnerIdsIn(assignmentsPartnerIds) } returns emptyList()

        every { partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
            assignmentsPartnerIds, approvedAndAfterApprovedApplicationStatuses) } returns emptyList()

        assertThrows<ProjectPartnerNotValidException> { assignInstitutionToPartner.assignInstitutionToPartner(newAssignment) }
    }

}
