package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanAssignInstitutionToPartner
import io.cloudflight.jems.server.controllerInstitution.service.institutionPartnerAssignmentsChanged
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionUsersProjectAssignment
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignInstitutionToPartner(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val partnerPersistence: PartnerPersistenceProvider,
    private val auditPublisher: ApplicationEventPublisher,
) : AssignInstitutionToPartnerInteractor {

    @CanAssignInstitutionToPartner
    @ExceptionWrapper(AssignInstitutionToPartnerException::class)
    @Transactional
    override fun assignInstitutionToPartner(institutionPartnerAssignments: ControllerInstitutionAssignment): List<InstitutionPartnerAssignment> {

        val assignmentsToRemove = institutionPartnerAssignments.assignmentsToRemove
        val assignmentsToSaveOrUpdate = institutionPartnerAssignments.assignmentsToAdd

        val assignmentsPartnerIds = assignmentsToSaveOrUpdate.map { it.partnerId }.union(assignmentsToRemove.map { it.partnerId })
        val existingAssignmentsBeforeUpdate = controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByPartnerIdsIn(assignmentsPartnerIds)

        val partnerToProjectIdMap = partnerPersistence.getPartnerProjectIdByPartnerIdAndProjectStatusIn(
            assignmentsPartnerIds, setOf(
                ApplicationStatus.APPROVED,
                ApplicationStatus.MODIFICATION_PRECONTRACTING,
                ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED,
                ApplicationStatus.CONTRACTED,
                ApplicationStatus.IN_MODIFICATION,
                ApplicationStatus.MODIFICATION_SUBMITTED,
                ApplicationStatus.MODIFICATION_REJECTED
            )
        ).associateBy(keySelector = { it.first }, valueTransform = { it.second })


        validateAssignments(assignmentsToSaveOrUpdate.union(assignmentsToRemove), partnerToProjectIdMap)
        assignmentsToSaveOrUpdate.forEach { it.setPartnerProjectId(partnerToProjectIdMap[it.partnerId]!!) }
        assignmentsToRemove.forEach { it.setPartnerProjectId(partnerToProjectIdMap[it.partnerId]!!) }


        return controllerInstitutionPersistence.assignInstitutionToPartner(
            assignmentsToRemove = assignmentsToRemove,
            assignmentsToSave = assignmentsToSaveOrUpdate
        ).also {
            updateInstitutionUsersProjectAssignment(
                assignmentsToSaveOrUpdate,
                assignmentsToRemove,
                existingAssignmentsBeforeUpdate,
            )
            auditPublisher.publishEvent(
                institutionPartnerAssignmentsChanged(
                    context = this,
                    assignmentsToSaveOrUpdate,
                    assignmentsToRemove
                )
            )
        }
    }

    private fun updateInstitutionUsersProjectAssignment(
        savedOrUpdatedAssignments: List<InstitutionPartnerAssignment>,
        removedAssignments: List<InstitutionPartnerAssignment>,
        existingAssignmentsBeforeUpdate: List<InstitutionPartnerAssignment>,
    ) {
        val institutionIds =
            savedOrUpdatedAssignments.map { it.institutionId }
                .union(removedAssignments.map { it.institutionId })
                .union(existingAssignmentsBeforeUpdate.map { it.institutionId })
        val institutionUsers = getInstitutionUsers(institutionIds)
        val projectIds = savedOrUpdatedAssignments.map { it.partnerProjectId }.union(removedAssignments.map { it.partnerProjectId })
        val currentAssignmentsWithUsers = getCurrentAssignmentsWithUsersByProjectIds(projectIds)

        val projectIdToAssignedInstitutionUsersMap =
            currentAssignmentsWithUsers.groupBy(keySelector = { it.partnerProjectId }, valueTransform = { it.userId })
        val projectToInstitutionUserIdsMap = mutableMapOf<Long, InstitutionUsersProjectAssignment>()

        removedAssignments.forEach { removedAssignment ->
            val removedInstitutionAssignmentUsers = institutionUsers[removedAssignment.institutionId]?.toSet() ?: emptySet()
            val existingUsersAssignedToProject = projectIdToAssignedInstitutionUsersMap[removedAssignment.partnerProjectId]?.toSet() ?: emptySet()
            projectToInstitutionUserIdsMap.setInstitutionUsersProjectAssignment(
                projectId = removedAssignment.partnerProjectId,
                userIdsToRemove = removedInstitutionAssignmentUsers.minus(existingUsersAssignedToProject)
            )
        }

        savedOrUpdatedAssignments.forEach { newAssignment ->
            val usersAssignedToProject = projectIdToAssignedInstitutionUsersMap[newAssignment.partnerProjectId]?.toSet() ?: emptySet()
            val usersToRemove = getUpdatedInstitutionPartnerAssignment(newAssignment, existingAssignmentsBeforeUpdate)
                .takeIf { it !== null }?.let { updatedAssignment -> institutionUsers[updatedAssignment.institutionId]?.toSet()?.minus(usersAssignedToProject) }

            projectToInstitutionUserIdsMap.setInstitutionUsersProjectAssignment(
                projectId = newAssignment.partnerProjectId,
                userIdsToRemove = usersToRemove ?: emptySet(),
                userIdsToAdd = institutionUsers[newAssignment.institutionId]?.toSet() ?: emptySet()
            )
        }

        updateInstitutionUsersProjectAssignment(projectToInstitutionUserIdsMap)
    }


    private fun updateInstitutionUsersProjectAssignment(projectUsers: Map<Long, InstitutionUsersProjectAssignment>) {
        projectUsers.keys.forEach { projectId ->
            projectUsers[projectId]?.let { it ->
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
            .firstOrNull { assignmentBeforeUpdate -> assignmentBeforeUpdate.partnerId == newAssignment.partnerId &&
                assignmentBeforeUpdate.institutionId != newAssignment.institutionId }
        return existingAssignmentBeforeUpdate
    }


    private fun getInstitutionUsers(institutionIds: Set<Long>): Map<Long, List<Long>> {
        return controllerInstitutionPersistence.getControllerInstitutionUsersByInstitutionIds(institutionIds)
            .groupBy({ it.institutionId }, { it.userId })
    }

    private fun getCurrentAssignmentsWithUsersByProjectIds(projectIds: Set<Long>) =
        controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(projectIds)


    private fun validateAssignments(
        assignmentsToValidate: Set<InstitutionPartnerAssignment>,
        partnersToProjectIdMap: Map<Long, Long>
    ) {
        val notValidAssignments =
            assignmentsToValidate.filter { partnersToProjectIdMap.containsKey(it.partnerId).not() }
        notValidAssignments.takeIf { it.isNotEmpty() }?.let {
            throw ProjectPartnerNotValidException()
        }
    }

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
    private fun InstitutionPartnerAssignment.setPartnerProjectId(projectId: Long) {
        this.partnerProjectId = projectId
    }

}
