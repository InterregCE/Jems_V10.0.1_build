package io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
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
import org.springframework.context.ApplicationEventPublisher

class CheckInstitutionPartnerAssignmentsTest : UnitTest() {
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
                partnerProjectId = 2L
            ),
            InstitutionPartnerAssignment(
                institutionId = 3L,
                partnerId = 3L,
                partnerProjectId = 3L
            )
        )

        private fun projectSummary(id: Long) = mockk<ProjectSummary> {
            every { this@mockk.id } returns id
            every { customIdentifier } returns id.toString()
            every { acronym } returns id.toString()
        }
    }

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var checkInstitutionPartnerAssignments: CheckInstitutionPartnerAssignments

    @BeforeEach
    fun resetMocks() {
        clearMocks(controllerInstitutionPersistence)
        clearMocks(auditPublisher)
    }

    @Test
    fun checkInstitutionAssignmentsToRemoveForUpdatedPartners() {
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsToDeleteByProjectId(PROJECT_ID) } returns institutionPartnerAssignments

        every {
            controllerInstitutionPersistence.assignInstitutionToPartner(
                partnerIdsToRemove = setOf(1L, 2L, 3L),
                assignmentsToSave = emptyList()
            )
        } returns institutionPartnerAssignments
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(1L)
        every { projectPersistence.getProjectSummary(2L) } returns projectSummary(2L)
        every { projectPersistence.getProjectSummary(3L) } returns projectSummary(3L)

        checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedPartners(1L)

        verify(exactly = 1) {
            controllerInstitutionPersistence.assignInstitutionToPartner(
                partnerIdsToRemove = setOf(1L, 2L, 3L),
                assignmentsToSave = emptyList()
            )
        }
        verifyAuditEvents(listOf(1L, 2L, 3L))
    }

    @Test
    fun checkInstitutionAssignmentsToRemoveForUpdatedInstitution() {
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(INSTITUTION_ID) } returns institutionPartnerAssignments

        every {
            controllerInstitutionPersistence.assignInstitutionToPartner(
                partnerIdsToRemove = setOf(1L, 2L, 3L),
                assignmentsToSave = emptyList()
            )
        } returns institutionPartnerAssignments
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(1L)
        every { projectPersistence.getProjectSummary(2L) } returns projectSummary(2L)
        every { projectPersistence.getProjectSummary(3L) } returns projectSummary(3L)

        checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedInstitution(1L)

        verify(exactly = 1) {
            controllerInstitutionPersistence.assignInstitutionToPartner(
                partnerIdsToRemove = setOf(1L, 2L, 3L),
                assignmentsToSave = emptyList()
            )
        }
        verifyAuditEvents(listOf(1L, 2L, 3L))
     }

    private fun verifyAuditEvents(ids: List<Long>) {
        val auditCapture = mutableListOf<AuditCandidateEvent>()
        verify(exactly = ids.size) { auditPublisher.publishEvent(capture(auditCapture)) }
        assertThat(auditCapture).extracting({ it.auditCandidate.action }, { it.auditCandidate.project }, { it.auditCandidate.description })
            .containsExactly(*ids.map { auditCandidateTuple(it) }.toTypedArray())
    }

    private fun auditCandidateTuple(id: Long) = tuple(
        AuditAction.INSTITUTION_PARTNER_ASSIGNMENT_DROPPED,
        AuditProject("$id", "$id", "$id"),
        "User ID: 0 User email: System\nInstitutionID: $id - PartnerID: $id"
    )!!
}
