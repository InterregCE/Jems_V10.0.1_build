package io.cloudflight.jems.server.programme.service.indicator.get_result_indicator

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetResultIndicator(
    private val persistence: ResultIndicatorPersistence
) : GetResultIndicatorInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(GetResultIndicatorException::class)
    override fun getResultIndicator(id: Long) =
        persistence.getResultIndicator(id)

}
