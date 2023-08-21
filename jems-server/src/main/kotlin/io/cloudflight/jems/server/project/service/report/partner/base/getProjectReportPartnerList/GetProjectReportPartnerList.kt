package io.cloudflight.jems.server.project.service.report.partner.base.getProjectReportPartnerList

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
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
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val securityService: SecurityService,
) : GetProjectReportPartnerListInteractor {

    @CanRetrieveProjectPartnerReports
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportPartnerListException::class)
    override fun findAllByProjectId(projectId: Long, sort: Sort, version: String?): List<ProjectPartnerSummary> {
        val partners = persistence.findAllByProjectIdForDropdown(projectId, sort, version)

        val allowedPartners = getAllowedPartners(projectId, partners)

        val institutionsByPartnerIds = controllerInstitutionPersistence.getControllerInstitutions(
            partnerIds = allowedPartners.mapNotNull { it.id }.toSet()
        )

        return allowedPartners.fillInInstitutions(institutionsByPartnerIds)
    }

    private fun getAllowedPartners(
        projectId: Long,
        partners: List<ProjectPartnerSummary>
    ): List<ProjectPartnerSummary> {
        if (canThisUserViewReporting(projectId)) {
            return partners
        }
        val userId = securityService.getUserIdOrThrow()

        val partnerIdsFromCollaborators = findPartnersForCurrentUserInProjectCollaborators(projectId, userId = userId)
        val partnerIdsFromControllers = findPartnersForCurrentUserInControllerInstitutions(projectId, userId = userId)

        return partners.onlyThose(allowedIds = (partnerIdsFromCollaborators union partnerIdsFromControllers))
    }

    private fun findPartnersForCurrentUserInProjectCollaborators(projectId: Long, userId: Long): Set<Long> =
        partnerCollaboratorPersistence.findPartnersByUserAndProject(userId = userId, projectId)
            .mapTo(HashSet()) { it.partnerId }

    private fun findPartnersForCurrentUserInControllerInstitutions(projectId: Long, userId: Long) =
        controllerInstitutionPersistence.getRelatedProjectAndPartnerIdsForUser(userId = userId)
            .getOrDefault(projectId, emptySet())

    private fun canThisUserViewReporting(projectId: Long) =
        userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, projectId)

    private fun List<ProjectPartnerSummary>.onlyThose(allowedIds: Set<Long>) =
        filter { it.id in allowedIds }

    private fun List<ProjectPartnerSummary>.fillInInstitutions(institutions: Map<Long, ControllerInstitutionList>) =
        onEach { it.institutionName = institutions[it.id!!]?.name }

}
