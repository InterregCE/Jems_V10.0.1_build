package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingManagementEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingReportingView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingManagementView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractsEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractsView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreatorContractingReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreatorContractingReportingView
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canViewContractsAndAgreements(#projectId)")
annotation class CanViewContractsAndAgreements

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canEditContractsAndAgreements(#projectId)")
annotation class CanEditContractsAndAgreements

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canViewProjectManagers(#projectId)")
annotation class CanViewProjectManagers

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canEditProjectManagers(#projectId)")
annotation class CanEditProjectManagers

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canViewReportingSchedule(#projectId) || " +
    "@projectMonitoringAuthorization.canViewProjectMonitoring(#projectId)")
annotation class CanRetrieveProjectReportingSchedule

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canEditReportingSchedule(#projectId)")
annotation class CanEditProjectReportingSchedule

@Component
class ProjectContractingManagementAuthorization(
    override val securityService: SecurityService,
    val partnerPersistence: PartnerPersistence,
    val projectPersistence: ProjectPersistence,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : Authorization(securityService) {

    fun canViewContractsAndAgreements(projectId: Long) = canView(ContractingPermission.ViewContractsAgreements, projectId)
    fun canEditContractsAndAgreements(projectId: Long) = canEdit(ContractingPermission.EditContractsAgreements, projectId)

    fun canViewProjectManagers(projectId: Long) = canView(ContractingPermission.ViewManagers, projectId)
    fun canEditProjectManagers(projectId: Long) = canEdit(ContractingPermission.EditManagers, projectId)

    fun canViewReportingSchedule(projectId: Long) = canView(ContractingPermission.ViewSchedule, projectId)
    fun canEditReportingSchedule(projectId: Long) = canEdit(ContractingPermission.EditSchedule, projectId)

    private fun canView(needed: ContractingPermission, projectId: Long): Boolean {
        // monitor users
        if (hasPermission(needed.monitorPermission, projectId))
            return true

        // partner collaborators and project collaborators
        val partnerCollaborators = partnerCollaboratorRepository.findAllByProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val projectCollaborators = projectCollaboratorRepository.findAllByIdProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val collaborators = partnerCollaborators union projectCollaborators
        if (isActiveUserIdEqualToOneOf(collaborators) && hasPermissionIfPresent(needed.creatorPermission))
            return true

        // controllers
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        if (isActiveUserIdEqualToOneOf(partnerControllers) && hasNonProjectAuthority(needed.monitorPermission))
            return true

        return false
    }

    private fun canEdit(needed: ContractingPermission, projectId: Long): Boolean {
        // monitor users
        if (hasPermission(needed.monitorPermission, projectId))
            return true

        // partner collaborators and project collaborators
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        if (isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel()) && hasPermissionIfPresent(needed.creatorPermission))
            return true

        // controllers
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        if (isActiveUserIdEqualToOneOf(partnerControllers) && hasNonProjectAuthority(needed.monitorPermission))
            return true

        return false
    }

    private fun hasPermissionIfPresent(permission: UserRolePermission?) =
        if (permission == null)
            true
        else
            hasPermission(permission)

    enum class ContractingPermission(val monitorPermission: UserRolePermission, val creatorPermission: UserRolePermission?) {
        ViewContractsAgreements(ProjectContractsView, null),
        EditContractsAgreements(ProjectContractsEdit, null),
        ViewManagers(ProjectContractingManagementView, null),
        EditManagers(ProjectContractingManagementEdit, null),
        ViewSchedule(ProjectContractingReportingView, ProjectCreatorContractingReportingView),
        EditSchedule(ProjectContractingReportingEdit, ProjectCreatorContractingReportingEdit),
    }

}
