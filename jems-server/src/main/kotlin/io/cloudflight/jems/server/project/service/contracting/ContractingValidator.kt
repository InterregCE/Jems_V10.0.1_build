package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.server.common.validator.EMAIL_REGEX
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.stereotype.Service

@Service
class ContractingValidator(private val validator: GeneralValidatorService) {

    companion object {
        const val MAX_NUMBER_OF_ADD_DATES = 25
    }

    fun validateManagerContacts(projectManagers: List<ProjectContractingManagement>) {
        projectManagers.forEach { contact -> validateContact(contact) }
    }

    fun validateProjectStepAndStatus(projectSummary: ProjectSummary) {
        if (projectSummary.isInStep1() ||
            (projectSummary.isInStep2() && projectSummary.isNotApprovedOrAnyStatusAfterApproved())
        ) {
            throw ContractingDeniedException()
        }
    }

    fun validateProjectStatusForModification(projectSummary: ProjectSummary) {
        if (!projectSummary.status.isAlreadyApproved()
            && !projectSummary.status.isModifiableStatusAfterApproved()
            && !projectSummary.status.isModificationSubmitted()) {
            throw ContractingModificationDeniedException()
        }
    }

    fun validateMonitoringInput(monitoring: ProjectContractingMonitoring) =
        validator.throwIfAnyIsInvalid(
            validator.maxSize(
                monitoring.addDates, MAX_NUMBER_OF_ADD_DATES, "addDates"
            )
        )

    private fun validateContact(managerContact: ProjectContractingManagement) {
        validator.throwIfAnyIsInvalid(
            validator.maxLength(managerContact.title, 25, "title"),
            validator.maxLength(managerContact.firstName, 50, "firstName"),
            validator.maxLength(managerContact.lastName, 50, "lastName"),
            validator.maxLength(managerContact.email, 255, "email"),
            if (managerContact.email.isNullOrBlank()) emptyMap () else
                validator.matches(managerContact.email, EMAIL_REGEX, "email", "project.contact.email.wrong.format"),
            validator.maxLength(managerContact.telephone, 25, "telephone"),
        )

    }

    fun ProjectSummary.isNotApprovedOrAnyStatusAfterApproved(): Boolean =
        status !in listOf(
            ApplicationStatus.APPROVED,
            ApplicationStatus.CONTRACTED,
            ApplicationStatus.APPROVED_WITH_CONDITIONS,
            ApplicationStatus.MODIFICATION_PRECONTRACTING,
            ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED,
            ApplicationStatus.IN_MODIFICATION,
            ApplicationStatus.MODIFICATION_SUBMITTED,
            ApplicationStatus.MODIFICATION_REJECTED
        )

}
