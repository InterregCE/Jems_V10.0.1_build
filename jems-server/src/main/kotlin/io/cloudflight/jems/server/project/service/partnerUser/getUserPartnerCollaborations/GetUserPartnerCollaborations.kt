package io.cloudflight.jems.server.project.service.partnerUser.getUserPartnerCollaborations

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorPersistenceProvider
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class GetUserPartnerCollaborations(
    private val userPartnerCollaboratorPersistence: UserPartnerCollaboratorPersistenceProvider,
    private val securityService: SecurityService
): GetUserPartnerCollaborationsInteractor {

    // no security needed

    @ExceptionWrapper(GetUserPartnerCollaborationsException::class)
    @Transactional(readOnly = true)
    override fun getUserPartnerCollaborations(projectId: Long): Set<PartnerCollaborator> {
       return userPartnerCollaboratorPersistence.findPartnersByUserAndProject(securityService.getUserIdOrThrow(), projectId)
    }
}
