package io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.updateInstitutionUsersProjectAssignment.UpdateInstitutionUsersProjectAssignmentInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.context.ApplicationEventPublisher

class CheckInstitutionPartnerAssignmentsTest: UnitTest() {
    companion object {
        private const val PROJECT_ID = 1L
        private const val INSTITUTION_ID = 1L

       private val institutionPartnerAssignments = listOf(
            InstitutionPartnerAssignment(
                institutionId = 1L,
                partnerId = 1L,
                partnerProjectId = 1L
            ),
            InstitutionPartnerAssignment(
                institutionId = 2L,
                partnerId = 2L,
                partnerProjectId = 1L
            ),
            InstitutionPartnerAssignment(
                institutionId = 5L,
                partnerId = 3L,
                partnerProjectId = 2L
            )
        )
    }

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var updateInstitutionUsersProjectAssignment: UpdateInstitutionUsersProjectAssignmentInteractor

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var checkInstitutionPartnerAssignments: CheckInstitutionPartnerAssignments


    @BeforeEach
    fun resetMocks() {
        clearMocks(controllerInstitutionPersistence)
        clearMocks(updateInstitutionUsersProjectAssignment)
        clearMocks(auditPublisher)
    }


    @Test
    fun checkInstitutionAssignmentsToRemoveForUpdatedPartners() {
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsToDeleteByProjectId(PROJECT_ID) } returns institutionPartnerAssignments

        every { controllerInstitutionPersistence.assignInstitutionToPartner(
           assignmentsToRemove = institutionPartnerAssignments,
           assignmentsToSave = emptyList()
        ) } returns institutionPartnerAssignments


        checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedPartners(1L)

        verify(exactly = 1) {  controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = institutionPartnerAssignments,
            assignmentsToSave = emptyList()
        )}
        verify(exactly = 1) { updateInstitutionUsersProjectAssignment.updateInstitutionUsersProjectAssignment(
            removedAssignments = institutionPartnerAssignments,
            savedOrUpdatedAssignments = emptyList(),
            existingAssignmentsBeforeUpdate = institutionPartnerAssignments
        )}
        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        with(slotAudit.captured.auditCandidate) {
            Assertions.assertThat(action).isEqualTo(AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_DROPPED)
            Assertions.assertThat(description.contains(
                """
                User ID: 0 User email: System
                InstitutionID: 1 - PartnerID: 1
                InstitutionID: 2 - PartnerID: 2
                InstitutionID: 5 - PartnerID: 3
                """
            ))
        }

    }

    @Test
    fun checkInstitutionAssignmentsToRemoveForUpdatedInstitution() {
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(INSTITUTION_ID) } returns institutionPartnerAssignments

        every { controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = institutionPartnerAssignments,
            assignmentsToSave = emptyList()
        ) } returns institutionPartnerAssignments

        checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedInstitution(1L)

        verify(exactly = 1) {  controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = institutionPartnerAssignments,
            assignmentsToSave = emptyList()
        )}
        verify(exactly = 1) { updateInstitutionUsersProjectAssignment.updateInstitutionUsersProjectAssignment(
            removedAssignments = institutionPartnerAssignments,
            savedOrUpdatedAssignments = emptyList(),
            existingAssignmentsBeforeUpdate = institutionPartnerAssignments
        )}
        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        with(slotAudit.captured.auditCandidate) {
            Assertions.assertThat(action).isEqualTo(AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_DROPPED)
            Assertions.assertThat(description.contains(
                """
                User ID: 0 User email: System
                InstitutionID: 1 - PartnerID: 1
                InstitutionID: 2 - PartnerID: 2
                InstitutionID: 5 - PartnerID: 3
                """
            ))
        }
    }
}
