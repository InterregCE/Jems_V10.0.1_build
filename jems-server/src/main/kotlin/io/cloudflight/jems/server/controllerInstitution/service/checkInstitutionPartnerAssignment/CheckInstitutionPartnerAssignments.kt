package io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.institutionPartnerAssignmentRemoved
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.project.service.ProjectPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckInstitutionPartnerAssignments(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val projectPersistence: ProjectPersistence,
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

    override fun checkInstitutionAssignmentsToRemoveForUpdatedInstitution(institutionId: Long) {
        controllerInstitutionPersistence.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(institutionId)
            .takeIf { it.isNotEmpty() }?.let { assignmentsToDelete ->
                deleteInstitutionPartnerAssignments(assignmentsToDelete)
            }
    }

    private fun deleteInstitutionPartnerAssignments(assignmentsToDelete: List<InstitutionPartnerAssignment>) {
        controllerInstitutionPersistence.assignInstitutionToPartner(
            partnerIdsToRemove = assignmentsToDelete.mapTo(HashSet()) { it.partnerId },
            assignmentsToSave = emptyList()
        )

        institutionPartnerAssignmentRemoved(
            context = this,
            deletedAssignments = assignmentsToDelete,
            projectResolver = { projectPersistence.getProjectSummary(it) }
        ).forEach { event -> auditPublisher.publishEvent(event) }

    }
}
