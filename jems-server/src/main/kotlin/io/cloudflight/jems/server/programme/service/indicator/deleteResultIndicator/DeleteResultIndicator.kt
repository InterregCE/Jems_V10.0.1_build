package io.cloudflight.jems.server.programme.service.indicator.deleteResultIndicator

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorDeleted
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import org.springframework.transaction.annotation.Transactional


@Service
class DeleteResultIndicator(
    private val persistence: ResultIndicatorPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditPublisher: ApplicationEventPublisher
): DeleteResultIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(ResultIndicatorDeletionFailed::class)
    override fun deleteResultIndicator(resultIndicatorId: Long){
        if (isProgrammeSetupLocked.isLocked())
            throw ResultIndicatorDeletionWhenProgrammeSetupRestricted()

        val resultIndicatorToBeDeleted = persistence.getResultIndicator(resultIndicatorId)
        persistence.deleteResultIndicator(resultIndicatorId).also {
            auditPublisher.publishEvent(
                indicatorDeleted(this, resultIndicatorToBeDeleted.identifier)
            )
        }
    }
}
