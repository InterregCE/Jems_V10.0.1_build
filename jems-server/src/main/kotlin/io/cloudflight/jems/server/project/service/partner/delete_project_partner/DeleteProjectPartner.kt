package io.cloudflight.jems.server.project.service.partner.delete_project_partner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartner(
    private val persistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence
) : DeleteProjectPartnerInteractor {

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerException::class)
    override fun deletePartner(partnerId: Long) =
        ifCanBeDeleted(partnerId).run {
            persistence.deletePartner(partnerId)
        }

    fun ifCanBeDeleted(partnerId: Long) {
        projectPersistence.getProjectSummary(persistence.getProjectIdForPartnerId(partnerId)).let { projectSummary ->
            if (!projectSummary.status.isModifiableStatusBeforeApproved())
                throw PartnerCannotBeDeletedException()
        }
    }
}
