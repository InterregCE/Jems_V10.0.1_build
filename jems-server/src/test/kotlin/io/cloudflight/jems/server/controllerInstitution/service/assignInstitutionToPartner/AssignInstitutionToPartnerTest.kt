package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.controllerInstitution.*
import io.cloudflight.jems.server.controllerInstitution.service.model.*
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
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
    lateinit var  userProjectPersistence: UserProjectPersistence

    @RelaxedMockK
    lateinit var  partnerPersistence: PartnerPersistenceProvider

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher


    @InjectMockKs
    lateinit var assignInstitutionToPartner: AssignInstitutionToPartner


    @BeforeEach
    fun resetMocks() {
        clearMocks(controllerInstitutionPersistence)
        clearMocks(partnerPersistence)
        clearMocks(userProjectPersistence)
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


    @Test
    fun `assigned institution to partner also assigns institution users to partner project`() {
        val partnerProjectId = 1L
        val partnerId = 1L
        val newAssignment = ControllerInstitutionAssignment(
            assignmentsToAdd = listOf(institutionAssignment),
            assignmentsToRemove = emptyList()
        )
        val institutionUser = ControllerInstitutionUser(
            INSTITUTION_ID,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )
        val assignmentsPartnerIds = newAssignment.assignmentsToAdd.map { it.partnerId }.union(newAssignment.assignmentsToRemove.map { it.partnerId })

        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByPartnerIdsIn(assignmentsPartnerIds) } returns emptyList()
        every { partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
            assignmentsPartnerIds, approvedAndAfterApprovedApplicationStatuses) } returns listOf(Pair(partnerId, partnerProjectId))
        every { controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = emptyList(),
            assignmentsToSave = listOf(InstitutionPartnerAssignment(
                institutionId = INSTITUTION_ID,
                partnerId = partnerId,
                partnerProjectId = partnerProjectId
            ))
        ) }  returns listOf(InstitutionPartnerAssignment(institutionId = INSTITUTION_ID, partnerId = partnerId, partnerProjectId = partnerProjectId))

        every { controllerInstitutionPersistence.getControllerInstitutionUsersByInstitutionIds(setOf(INSTITUTION_ID)) } returns listOf(
            institutionUser
        )
        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(setOf(partnerProjectId))
        } returns listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = INSTITUTION_ID,
                userId = institutionUser.userId,
                partnerProjectId = partnerProjectId
            )
        )
        every {
            userProjectPersistence.changeUsersAssignedToProject(
                partnerProjectId,
                userIdsToAssign = setOf(institutionUser.userId),
                userIdsToRemove = emptySet()
            )
        } returns setOf(institutionUser.userId)


        assertThat(assignInstitutionToPartner.assignInstitutionToPartner(newAssignment)).containsExactly(
            InstitutionPartnerAssignment(
                institutionId = INSTITUTION_ID,
                partnerId = partnerId,
                partnerProjectId = partnerProjectId
            )
        )

        assertThat( userProjectPersistence.changeUsersAssignedToProject(
                partnerProjectId,
                userIdsToAssign = setOf(institutionUser.userId),
                userIdsToRemove = emptySet()
            )
        ).containsExactly(MONITOR_USER_1_ID)
    }

    @Test
    fun `institution user is NOT removed from project assignment if part of another institution assignment of the same project`() {
        val institutionA_id = 1L
        val institutionB_id = 2L
        val partnerProjectId = 1L
        val partnerId = 1L

        val institutionAssignment = InstitutionPartnerAssignment(
            institutionId = institutionA_id,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        val updatedAssignment = ControllerInstitutionAssignment(
            assignmentsToAdd = emptyList(),
            assignmentsToRemove = listOf(institutionAssignment)
        )
        val institutionUser = ControllerInstitutionUser(
            institutionA_id,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )

        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByPartnerIdsIn(setOf(partnerId)) } returns listOf(institutionAssignment)
        every { partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
            setOf(partnerId), approvedAndAfterApprovedApplicationStatuses) } returns listOf(Pair(partnerId, partnerProjectId))
        every { controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = listOf(InstitutionPartnerAssignment(
                institutionId = institutionA_id,
                partnerId = partnerId,
                partnerProjectId = partnerProjectId
            )),
            assignmentsToSave = emptyList()
        ) }  returns emptyList()

        every { controllerInstitutionPersistence.getControllerInstitutionUsersByInstitutionIds(setOf(institutionA_id)) } returns listOf(
            institutionUser
        )
        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(setOf(partnerProjectId))
        } returns listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = institutionA_id,
                userId = institutionUser.userId,
                partnerProjectId = partnerProjectId
            ),
            InstitutionPartnerAssignmentWithUsers(
                institutionId = institutionB_id,
                userId = institutionUser.userId,
                partnerProjectId = partnerProjectId
            )
        )
        every {
            userProjectPersistence.changeUsersAssignedToProject(
                partnerProjectId,
                userIdsToAssign = emptySet(),
                userIdsToRemove = emptySet()
            )
        } returns setOf(institutionUser.userId)

        assertThat(assignInstitutionToPartner.assignInstitutionToPartner(updatedAssignment).isEmpty()).isTrue

        assertThat( userProjectPersistence.changeUsersAssignedToProject(
            partnerProjectId,
            userIdsToAssign = emptySet(),
            userIdsToRemove = emptySet()
        )
        ).containsExactly(MONITOR_USER_1_ID)
    }



    @Test
    fun `update institution assignment - user project assignment is updated` () {
        val existingInstitutionId = 1L
        val newInstitutionId = 2L
        val partnerProjectId = 1L
        val partnerId = 1L

        val existingInstitutionAssignment = InstitutionPartnerAssignment(
            institutionId = existingInstitutionId,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        val newInstitutionPartnerAssignment = InstitutionPartnerAssignment(
            institutionId = newInstitutionId,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        val updatedAssignment = ControllerInstitutionAssignment(
            assignmentsToAdd = listOf(newInstitutionPartnerAssignment),
            assignmentsToRemove = emptyList()
        )
        val existingInstitutionUser = ControllerInstitutionUser(
            existingInstitutionId,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )
        val newInstitutionUser = ControllerInstitutionUser(
            newInstitutionId,
            MONITOR_USER_3_ID,
            MONITOR_USER_3_EMAIL,
            UserInstitutionAccessLevel.Edit
        )
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByPartnerIdsIn(setOf(partnerId)) } returns listOf(
            existingInstitutionAssignment
        )
        every { partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
            setOf(partnerId), approvedAndAfterApprovedApplicationStatuses) } returns listOf(Pair(partnerId, partnerProjectId))

        every { controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = emptyList(),
            assignmentsToSave = listOf(newInstitutionPartnerAssignment)
        ) }  returns listOf(newInstitutionPartnerAssignment)

        every {
            controllerInstitutionPersistence.getControllerInstitutionUsersByInstitutionIds(
                setOf(newInstitutionId, existingInstitutionId)
            )
        } returns listOf(existingInstitutionUser, newInstitutionUser)

        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(setOf(partnerProjectId))
        } returns listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = newInstitutionId,
                userId = newInstitutionUser.userId,
                partnerProjectId = partnerProjectId
            )
        )


        every {
            userProjectPersistence.changeUsersAssignedToProject(
                partnerProjectId,
                userIdsToAssign = setOf(newInstitutionUser.userId),
                userIdsToRemove = setOf(existingInstitutionUser.userId)
            )
        } returns setOf(newInstitutionUser.userId)

        assertThat(assignInstitutionToPartner.assignInstitutionToPartner(updatedAssignment)).isEqualTo(
            listOf(newInstitutionPartnerAssignment)
        )

        assertThat(userProjectPersistence.changeUsersAssignedToProject(
            partnerProjectId,
            userIdsToAssign = setOf(newInstitutionUser.userId),
            userIdsToRemove = setOf(existingInstitutionUser.userId)
        )
        ).containsExactly(newInstitutionUser.userId)

    }

}
