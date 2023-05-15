package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.controllerInstitution.approvedAndAfterApprovedApplicationStatuses
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class AssignInstitutionToPartnerTest : UnitTest() {

    companion object {
        private const val INSTITUTION_ID = 1L

        private fun institutionAssignment(id: Long) = InstitutionPartnerAssignment(
            institutionId = id,
            partnerId = id,
            partnerProjectId = id
        )

        private fun projectSummary(ID: Long) = mockk<ProjectSummary> {
            every { id } returns ID
            every { customIdentifier } returns ID.toString()
            every { acronym } returns ID.toString()
        }
    }

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var partnerPersistence: PartnerPersistenceProvider

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var assignInstitutionToPartner: AssignInstitutionToPartner


    @BeforeEach
    fun resetMocks() {
        clearMocks(partnerPersistence, controllerInstitutionPersistence)
    }

    @Test
    fun `assign institution to partner`() {
        val newAssignment = ControllerInstitutionAssignment(
            assignmentsToAdd = listOf(institutionAssignment(1L), institutionAssignment(2L)),
            assignmentsToRemove = emptyList()
        )
        val assignmentsPartnerIds = newAssignment.assignmentsToAdd.map { it.partnerId }.union(newAssignment.assignmentsToRemove.map { it.partnerId })

        every {
            partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
                assignmentsPartnerIds, approvedAndAfterApprovedApplicationStatuses
            )
        } returns listOf(Pair(1L, 1L), Pair(2L, 2L))
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(1L)
        every { projectPersistence.getProjectSummary(2L) } returns projectSummary(2L)


        val assignmentList = listOf(
            InstitutionPartnerAssignment(institutionId = 1L, partnerId = 1L, partnerProjectId = 1L),
            InstitutionPartnerAssignment(institutionId = 2L, partnerId = 2L, partnerProjectId = 2L),
        )
        every {
            controllerInstitutionPersistence.assignInstitutionToPartner(partnerIdsToRemove = emptySet(), assignmentsToSave = assignmentList)
        } returns assignmentList

        assertThat(assignInstitutionToPartner.assignInstitutionToPartner(newAssignment)).containsExactly(assignmentList[0], assignmentList[1])

        val auditCapture = mutableListOf<AuditCandidateEvent>()
        verify(exactly = 2) { auditPublisher.publishEvent(capture(auditCapture)) }
        assertThat(auditCapture).extracting({ it.auditCandidate.action }, { it.auditCandidate.project }, { it.auditCandidate.description })
            .containsExactly(auditCandidateTuple(1L), auditCandidateTuple(2L))
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

        every {
            partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
                assignmentsPartnerIds, approvedAndAfterApprovedApplicationStatuses
            )
        } returns emptyList()

        assertThrows<ProjectPartnerNotValidException> { assignInstitutionToPartner.assignInstitutionToPartner(newAssignment) }
    }

    private fun auditCandidateTuple(id: Long) = tuple(
        AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_CHANGED,
        AuditProject("${id}", "${id}", "${id}"),
        "Assignment of institution to partner changed to:\nProjectID: ${id}, PartnerID: ${id}, InstitutionID: ${id}"
    )

}
