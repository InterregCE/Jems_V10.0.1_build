package io.cloudflight.jems.server.project.service.report.partner.base.getProjectReportPartnerList

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartnerReports
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportPartnerList(
    private val persistence: PartnerPersistence,
    private val userAuthorization: UserAuthorization,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val securityService: SecurityService
) : GetProjectReportPartnerListInteractor {

    @CanRetrieveProjectPartnerReports
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportPartnerListException::class)
    override fun findAllByProjectId(
        projectId: Long, sort: Sort, version: String?
    ): List<ProjectPartnerSummary> {
        val projectPartnerReports = persistence.findAllByProjectIdForDropdown(projectId, sort, version)
        if (canViewPartnerReporting(projectId) || canEditPartnerReporting(projectId)) {
            return projectPartnerReports;
        }
        val partnerCollaboratorsIds = findAllPartnerCollaboratorsIdsByProjectId(projectId);

        return projectPartnerReports.filter { projectPartnerSummary ->
            partnerCollaboratorsIds.contains(projectPartnerSummary.id)
        }
    }

    private fun findAllPartnerCollaboratorsIdsByProjectId(projectId: Long): List<Long> {
        return partnerCollaboratorPersistence.findPartnersByUserAndProject(
            securityService.getUserIdOrThrow(), projectId
        ).map { it.partnerId }
    }

    private fun canViewPartnerReporting(projectId: Long) =
        this.userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, projectId)

    private fun canEditPartnerReporting(projectId: Long) =
        this.userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingEdit, projectId)
}
