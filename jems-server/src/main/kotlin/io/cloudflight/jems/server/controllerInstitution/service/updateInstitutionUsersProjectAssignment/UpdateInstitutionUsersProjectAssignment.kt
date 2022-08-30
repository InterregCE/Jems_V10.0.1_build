package io.cloudflight.jems.server.controllerInstitution.service.updateInstitutionUsersProjectAssignment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionUsersProjectAssignment
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateInstitutionUsersProjectAssignment(
    private val userProjectPersistence: UserProjectPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence
) : UpdateInstitutionUsersProjectAssignmentInteractor {

    @Transactional
    @ExceptionWrapper(UpdateInstitutionUserProjectAssignmentException::class)
    override fun updateInstitutionUsersProjectAssignment(
        savedOrUpdatedAssignments: List<InstitutionPartnerAssignment>,
        removedAssignments: List<InstitutionPartnerAssignment>,
        existingAssignmentsBeforeUpdate: List<InstitutionPartnerAssignment>
    ) {
        val institutionIds =
            savedOrUpdatedAssignments.map { it.institutionId }
                .union(removedAssignments.map { it.institutionId })
                .union(existingAssignmentsBeforeUpdate.map { it.institutionId })
        val institutionUsers = getInstitutionUsers(institutionIds)
        val projectIds =
            savedOrUpdatedAssignments.map { it.partnerProjectId }.union(removedAssignments.map { it.partnerProjectId })
        val currentAssignmentsWithUsers = getCurrentAssignmentsWithUsersByProjectIds(projectIds)

        val projectIdToAssignedInstitutionUsersMap =
            currentAssignmentsWithUsers.groupBy(keySelector = { it.partnerProjectId }, valueTransform = { it.userId })
        val projectToInstitutionUserIdsMap = mutableMapOf<Long, InstitutionUsersProjectAssignment>()

        removedAssignments.forEach { removedAssignment ->
            val removedInstitutionAssignmentUsers =
                institutionUsers[removedAssignment.institutionId]?.toSet() ?: emptySet()
            val existingUsersAssignedToProject =
                projectIdToAssignedInstitutionUsersMap[removedAssignment.partnerProjectId]?.toSet() ?: emptySet()
            projectToInstitutionUserIdsMap.setInstitutionUsersProjectAssignment(
                projectId = removedAssignment.partnerProjectId,
                userIdsToRemove = removedInstitutionAssignmentUsers.minus(existingUsersAssignedToProject)
            )
        }

        savedOrUpdatedAssignments.forEach { newAssignment ->
            val usersAssignedToProject =
                projectIdToAssignedInstitutionUsersMap[newAssignment.partnerProjectId]?.toSet() ?: emptySet()
            val usersToRemove = getUpdatedInstitutionPartnerAssignment(newAssignment, existingAssignmentsBeforeUpdate)
                .takeIf { it !== null }?.let { updatedAssignment ->
                    institutionUsers[updatedAssignment.institutionId]?.toSet()?.minus(usersAssignedToProject)
                }

            projectToInstitutionUserIdsMap.setInstitutionUsersProjectAssignment(
                projectId = newAssignment.partnerProjectId,
                userIdsToRemove = usersToRemove ?: emptySet(),
                userIdsToAdd = institutionUsers[newAssignment.institutionId]?.toSet() ?: emptySet()
            )
        }

        updateUsersProjectAssignment(projectToInstitutionUserIdsMap)
    }

    private fun updateUsersProjectAssignment(projectUsers: Map<Long, InstitutionUsersProjectAssignment>) {
        projectUsers.keys.forEach { projectId ->
            projectUsers[projectId]?.let {
                userProjectPersistence.changeUsersAssignedToProject(
                    projectId,
                    userIdsToAssign = it.userIdsToAdd,
                    userIdsToRemove = it.userIdsToRemove
                )
            }
        }
    }

    private fun getUpdatedInstitutionPartnerAssignment(
        newAssignment: InstitutionPartnerAssignment,
        existingAssignmentsBeforeUpdate: List<InstitutionPartnerAssignment>
    ): InstitutionPartnerAssignment? {
        val existingAssignmentBeforeUpdate = existingAssignmentsBeforeUpdate
            .firstOrNull { assignmentBeforeUpdate ->
                assignmentBeforeUpdate.partnerId == newAssignment.partnerId &&
                    assignmentBeforeUpdate.institutionId != newAssignment.institutionId
            }
        return existingAssignmentBeforeUpdate
    }


    private fun getInstitutionUsers(institutionIds: Set<Long>): Map<Long, List<Long>> {
        return controllerInstitutionPersistence.getControllerInstitutionUsersByInstitutionIds(institutionIds)
            .groupBy({ it.institutionId }, { it.userId })
    }

    private fun getCurrentAssignmentsWithUsersByProjectIds(projectIds: Set<Long>) =
        controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(projectIds)


    private fun MutableMap<Long, InstitutionUsersProjectAssignment>.setInstitutionUsersProjectAssignment(
        projectId: Long,
        userIdsToAdd: Set<Long> = emptySet(),
        userIdsToRemove: Set<Long> = emptySet()
    ) {
        this.containsKey(projectId).let {
            if (it) {
                this[projectId]?.userIdsToRemove?.addAll(userIdsToRemove)
                this[projectId]?.userIdsToAdd?.addAll(userIdsToAdd)
            } else {
                this[projectId] = InstitutionUsersProjectAssignment(
                    userIdsToRemove = userIdsToRemove.toMutableSet(),
                    userIdsToAdd = userIdsToAdd.toMutableSet()
                )
            }
        }
    }

}
