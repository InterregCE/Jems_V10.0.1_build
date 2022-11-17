package io.cloudflight.jems.server.controllerInstitution.service.updateInstitutionUsersProjectAssignment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.*
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateInstitutionUsersProjectAssignmentTest: UnitTest() {

    companion object {
        private const val INSTITUTION_ID = 1L

        private val institutionAssignment = InstitutionPartnerAssignment(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerProjectId = 1L
        )
    }

    @RelaxedMockK
    lateinit var  controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var  userProjectPersistence: UserProjectPersistence

    @InjectMockKs
    lateinit var updateInstitutionUsersProjectAssignment: UpdateInstitutionUsersProjectAssignment


    @BeforeEach
    fun resetMocks() {
        clearMocks(controllerInstitutionPersistence)
        clearMocks(userProjectPersistence)
    }


    @Test
    fun `assigned institution to partner also assigns institution users to partner project`() {
        val partnerProjectId = 1L
        val savedAssignments = listOf(institutionAssignment)
        val institutionUser = ControllerInstitutionUser(
            INSTITUTION_ID,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )

        every { controllerInstitutionPersistence.getControllerInstitutionUsersByInstitutionIds(
            setOf(INSTITUTION_ID)
        ) } returns listOf(institutionUser)

        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(setOf(partnerProjectId))
        } returns listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = INSTITUTION_ID,
                userId = institutionUser.userId,
                partnerProjectId = partnerProjectId
            ),
            InstitutionPartnerAssignmentWithUsers(
                institutionId = 2L,
                userId = MONITOR_USER_2_ID,
                partnerProjectId = partnerProjectId
            )
        )
        val projectId = slot<Long>()
        val userIdsToRemove = slot<Set<Long>>()
        val userIdsToAdd = slot<Set<Long>>()
         every {
            userProjectPersistence.changeUsersAssignedToProject(capture(projectId), capture(userIdsToRemove), capture(userIdsToAdd))
        } returns setOf(institutionUser.userId)

        updateInstitutionUsersProjectAssignment.updateInstitutionUsersProjectAssignment(
            savedOrUpdatedAssignments = savedAssignments,
            removedAssignments = emptyList(),
            existingAssignmentsBeforeUpdate = emptyList()
        )

        Assertions.assertThat(projectId.captured).isEqualTo(partnerProjectId)
        Assertions.assertThat(userIdsToAdd.captured).containsExactly(7L)
        Assertions.assertThat(userIdsToRemove.captured).isEmpty()
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
        val institutionUser = ControllerInstitutionUser(
            institutionA_id,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )
        val assignmentsToRemove = listOf(InstitutionPartnerAssignment(
            institutionId = institutionA_id,
            partnerId = partnerId,
            partnerProjectId = partnerProjectId
        ))


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
        val projectId = slot<Long>()
        val userIdsToRemove = slot<Set<Long>>()
        val userIdsToAdd = slot<Set<Long>>()
        every {
            userProjectPersistence.changeUsersAssignedToProject(capture(projectId), capture(userIdsToRemove), capture(userIdsToAdd))
        } returns setOf(institutionUser.userId)

        updateInstitutionUsersProjectAssignment.updateInstitutionUsersProjectAssignment(
            savedOrUpdatedAssignments = emptyList(),
            removedAssignments = assignmentsToRemove,
            existingAssignmentsBeforeUpdate = listOf(institutionAssignment)
        )

        Assertions.assertThat(projectId.captured).isEqualTo(partnerProjectId)
        Assertions.assertThat(userIdsToAdd.captured).isEmpty()
        Assertions.assertThat(userIdsToRemove.captured).isEmpty()
    }


    @Test
    fun `change institution assignment - user project assignment is updated` () {
        val existingInstitutionId = 1L
        val newInstitutionId = 2L
        val partnerProjectId = 1L

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

        val projectId = slot<Long>()
        val userIdsToRemove = slot<Set<Long>>()
        val userIdsToAdd = slot<Set<Long>>()
        every {
            userProjectPersistence.changeUsersAssignedToProject(capture(projectId), capture(userIdsToRemove), capture(userIdsToAdd))
        } returns setOf(newInstitutionUser.userId)

        updateInstitutionUsersProjectAssignment.updateInstitutionUsersProjectAssignment(
            savedOrUpdatedAssignments = listOf(newInstitutionPartnerAssignment),
            removedAssignments = emptyList(),
            existingAssignmentsBeforeUpdate = listOf(existingInstitutionAssignment)
        )

        Assertions.assertThat(projectId.captured).isEqualTo(partnerProjectId)
        Assertions.assertThat(userIdsToAdd.captured).containsExactly(newInstitutionUser.userId)
        Assertions.assertThat(userIdsToRemove.captured).containsExactly(existingInstitutionUser.userId)

    }

}
