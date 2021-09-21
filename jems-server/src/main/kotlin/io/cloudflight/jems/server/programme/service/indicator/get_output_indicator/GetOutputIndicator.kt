package io.cloudflight.jems.server.programme.service.indicator.get_output_indicator

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetOutputIndicator(
    private val persistence: OutputIndicatorPersistence
) : GetOutputIndicatorInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(GetOutputIndicatorException::class)
    override fun getOutputIndicator(id: Long) =
        persistence.getOutputIndicator(id)
}
