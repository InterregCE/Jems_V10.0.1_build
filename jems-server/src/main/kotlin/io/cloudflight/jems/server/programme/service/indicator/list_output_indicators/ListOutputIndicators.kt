package io.cloudflight.jems.server.programme.service.indicator.list_output_indicators

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListOutputIndicators(
    private val persistence: OutputIndicatorPersistence
) : ListOutputIndicatorsInteractor {

    @Transactional(readOnly = true)
    @CanReadProgrammeSetup
    @ExceptionWrapper(GetOutputIndicatorDetailsException::class)
    override fun getOutputIndicatorDetails(pageable: Pageable) =
        persistence.getOutputIndicators(pageable)

    @Transactional(readOnly = true)
    @CanReadProgrammeSetup
    @ExceptionWrapper(GetOutputIndicatorSummariesException::class)
    override fun getOutputIndicatorSummaries() =
        persistence.getTop50OutputIndicators()

    @Transactional(readOnly = true)
    @CanReadProgrammeSetup
    @ExceptionWrapper(GetOutputIndicatorSummariesForSpecificObjectiveException::class)
    override fun getOutputIndicatorSummariesForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy) =
        persistence.getOutputIndicatorsForSpecificObjective(programmeObjectivePolicy)

}
