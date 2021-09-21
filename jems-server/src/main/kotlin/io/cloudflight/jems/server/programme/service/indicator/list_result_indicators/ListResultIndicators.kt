package io.cloudflight.jems.server.programme.service.indicator.list_result_indicators

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListResultIndicators(
    private val persistence: ResultIndicatorPersistence
) : ListResultIndicatorsInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(GetResultIndicatorDetailsException::class)
    override fun getResultIndicatorDetails(pageable: Pageable) =
        persistence.getResultIndicators(pageable)

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(GetResultIndicatorSummariesException::class)
    override fun getResultIndicatorSummaries() =
        persistence.getTop50ResultIndicators()

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(GetResultIndicatorSummariesForSpecificObjectiveException::class)
    override fun getResultIndicatorSummariesForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy) =
        persistence.getResultIndicatorsForSpecificObjective(programmeObjectivePolicy)

}
