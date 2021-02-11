package io.cloudflight.jems.server.programme.service.indicator.get_result_indicators_details

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.IndicatorResultPersistence
import org.springframework.stereotype.Service

@Service
class GetResultIndicatorDetails(
    private val indicatorResultPersistence: IndicatorResultPersistence
) : GetResultIndicatorDetailsInteractor {

    @CanReadProgrammeSetup
    override fun getResultIndicatorsDetails() =
        indicatorResultPersistence.getResultIndicatorsDetails()

    override fun getResultIndicatorsDetailsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy) =
        indicatorResultPersistence.getResultIndicatorsForSpecificObjective(programmeObjectivePolicy)

}
