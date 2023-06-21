package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.lockPartner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.projectContractingPartnerLocked
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContractingPartnerLock(
    private val contractingPartnerLockPersistence: ContractingPartnerLockPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : ContractingPartnerLockInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(ContractingPartnerLockException::class)
    override fun lockPartner(partnerId: Long, projectId: Long) {
        if (!contractingPartnerLockPersistence.isLocked(partnerId)) {
            contractingPartnerLockPersistence.lock(partnerId, projectId).also {
                val contractingPartner = partnerPersistence.getById(partnerId)
                auditPublisher.publishEvent(
                    projectContractingPartnerLocked(
                        context = this,
                        partner = contractingPartner,
                        projectId = projectId
                    )
                )
            }
        }
    }
}