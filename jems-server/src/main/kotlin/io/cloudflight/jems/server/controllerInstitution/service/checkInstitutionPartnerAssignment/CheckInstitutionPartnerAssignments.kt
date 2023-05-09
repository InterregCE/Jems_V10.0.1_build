package io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.institutionPartnerAssignmentRemoved
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckInstitutionPartnerAssignments(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : CheckInstitutionPartnerAssignmentsInteractor {

    @Transactional
    @ExceptionWrapper(CheckInstitutionPartnerAssignmentsException::class)
    override fun checkInstitutionAssignmentsToRemoveForUpdatedPartners(projectId: Long) {
        controllerInstitutionPersistence.getInstitutionPartnerAssignmentsToDeleteByProjectId(projectId)
            .takeIf { it.isNotEmpty() }?.let { assignmentsToDelete ->
                deleteInstitutionPartnerAssignments(assignmentsToDelete)
            }
    }

    private fun deleteInstitutionPartnerAssignments(assignmentsToDelete: List<InstitutionPartnerAssignment>) {
        controllerInstitutionPersistence.assignInstitutionToPartner(
            partnerIdsToRemove = assignmentsToDelete.mapTo(HashSet()) { it.partnerId },
            assignmentsToSave = emptyList()
        )
        auditPublisher.publishEvent(
            institutionPartnerAssignmentRemoved(
                context = this,
                deletedAssignments = assignmentsToDelete
            )
        )

    }

}
