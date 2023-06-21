package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.unlockPartner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.projectContractingPartnerUnlocked
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContractingPartnerUnlock(
    private val contractingPartnerLockPersistence: ContractingPartnerLockPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : ContractingPartnerUnlockInteractor {
    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(ContractingPartnerUnlockException::class)
    override fun unlockPartner(partnerId: Long, projectId: Long) {
        if (contractingPartnerLockPersistence.isLocked(partnerId)) {
            return contractingPartnerLockPersistence.unlock(partnerId).also {
                val contractingPartner = partnerPersistence.getById(partnerId)
                auditPublisher.publishEvent(
                    projectContractingPartnerUnlocked(
                        context = this,
                        partner = contractingPartner,
                        projectId = projectId
                    )
                )
            }

        }
    }

}