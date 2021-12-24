package io.cloudflight.jems.server.project.service.partner.deactivate_project_partner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeactivateProjectPartner(
    private val persistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence
) : DeactivateProjectPartnerInteractor {

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(DeactivateProjectPartnerException::class)
    override fun deactivate(partnerId: Long) =
        ifCanBeDeactivated(partnerId).run {
            persistence.deactivatePartner(partnerId)
        }

    fun ifCanBeDeactivated(partnerId: Long) {
        projectPersistence.getProjectSummary(persistence.getProjectIdForPartnerId(partnerId)).let { projectSummary ->
            if (!projectSummary.status.isModifiableStatusAfterApproved()) {
                throw PartnerCannotBeDeactivatedException()
            }
        }
    }
}
