package io.cloudflight.jems.server.project.service.contracting.partner.getPartners

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartners
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.model.partner.getPartners.ContractingPartnerSummary
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingPartners(
    private val partnerPersistence: PartnerPersistence,
    private val institutionPersistence: ControllerInstitutionPersistence,
    private val userPartnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val versionPersistence: ProjectVersionPersistence,
    private val contractingPartnerLockPersistence: ContractingPartnerLockPersistence,
    private val securityService: SecurityService,
    private val userAuthorization: UserAuthorization
): GetContractingPartnersInteractor {

    @CanRetrieveProjectContractingPartners
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingPartnersException::class)
    override fun findAllByProjectIdForContracting(projectId: Long, sort: Sort): List<ContractingPartnerSummary> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val partners = partnerPersistence.findAllByProjectIdForDropdown(projectId, sort, version)
        val lockedPartnerIds = contractingPartnerLockPersistence.getLockedPartners(projectId = projectId)

        if (this.userAuthorization.hasPermissionForProject(UserRolePermission.ProjectContractingPartnerView, projectId))
            return partners.withLocks(lockedPartnerIds)

        val partnerIdsFromCollaborators = findPartnersForCurrentUserInProjectCollaborators(projectId, securityService.getUserIdOrThrow())
        val partnerIdsFromControllers = findPartnersForCurrentUserInControllerInstitutions(projectId, securityService.getUserIdOrThrow())

        return partners
            .onlyThose(allowedIds = (partnerIdsFromCollaborators union partnerIdsFromControllers))
            .withLocks(lockedPartnerIds)
    }

    private fun List<ProjectPartnerSummary>.onlyThose(allowedIds: Set<Long>) = filter { it.id in allowedIds }
    private fun List<ProjectPartnerSummary>.withLocks(lockedPartnerIds: Set<Long>) = map {
        ContractingPartnerSummary(
            id = it.id!!,
            abbreviation = it.abbreviation,
            institutionName = it.institutionName,
            active = it.active,
            role = it.role,
            sortNumber = it.sortNumber,
            country = it.country,
            region = it.region,
            currencyCode = it.currencyCode,
            locked = it.id in lockedPartnerIds,
        )
    }

    private fun findPartnersForCurrentUserInProjectCollaborators(projectId: Long, userId: Long): Set<Long> {
        return userPartnerCollaboratorPersistence.findPartnersByUserAndProject(userId = userId, projectId)
            .mapTo(HashSet()) { it.partnerId }
    }

    private fun findPartnersForCurrentUserInControllerInstitutions(projectId: Long, userId: Long) =
        institutionPersistence.getRelatedProjectAndPartnerIdsForUser(userId = userId)
            .getOrDefault(projectId, emptySet())


}
