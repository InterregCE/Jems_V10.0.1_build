package io.cloudflight.jems.server.programme.service.indicator.deleteOutputIndicator

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorDeleted
import org.springframework.stereotype.Service
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional


@Service
class DeleteOutputIndicator(
    private val persistence: OutputIndicatorPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditPublisher: ApplicationEventPublisher
): DeleteOutputIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(OutputIndicatorDeletionFailed::class)
    override fun deleteOutputIndicator(outputIndicatorId: Long){
        if (isProgrammeSetupLocked.isLocked())
            throw OutputIndicatorDeletionWhenProgrammeSetupRestricted()

        val outputIndicatorToBeDeleted = persistence.getOutputIndicator(outputIndicatorId)
        persistence.deleteOutputIndicator(outputIndicatorId).also {
            auditPublisher.publishEvent(
                indicatorDeleted(this, outputIndicatorToBeDeleted.identifier)
            )
        }
    }
}
