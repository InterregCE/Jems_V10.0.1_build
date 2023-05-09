package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanAssignInstitutionToPartner
import io.cloudflight.jems.server.controllerInstitution.service.InstitutionPartnerAssignmentAudit
import io.cloudflight.jems.server.controllerInstitution.service.institutionPartnerAssignmentsChanged
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignInstitutionToPartner(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
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

        return controllerInstitutionPersistence.assignInstitutionToPartner(
            partnerIdsToRemove = assignmentsToRemove.mapTo(HashSet()) { it.partnerId },
            assignmentsToSave = assignmentsToSaveOrUpdate
        ).also {
            auditPublisher.publishEvent(
                institutionPartnerAssignmentsChanged(
                    context = this,
                    assignmentsToSaveOrUpdate.withProjectId { partnerId -> partnerToProjectIdMap[partnerId]!! },
                    assignmentsToRemove.withProjectId { partnerId -> partnerToProjectIdMap[partnerId]!! },
                )
            )
        }
    }

    private fun InstitutionPartnerAssignment.withProjectId(projectId: Long) = InstitutionPartnerAssignmentAudit(
        institutionId = institutionId!!,
        partnerId = partnerId,
        projectId = projectId,
    )

    private fun List<InstitutionPartnerAssignment>.withProjectId(partnerIdToProjectId: (Long) -> Long) = map {
        it.withProjectId(partnerIdToProjectId.invoke(it.partnerId))
    }

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

}
