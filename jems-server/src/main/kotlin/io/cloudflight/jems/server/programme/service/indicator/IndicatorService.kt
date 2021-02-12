package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorOutputDto
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputUpdate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultUpdate
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorOutput
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorResult
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IndicatorService {

    //region INDICATOR OUTPUT

    fun getOutputIndicatorById(id: Long): OutputIndicatorOutput

    fun getOutputIndicators(pageable: Pageable): Page<OutputIndicatorOutput>

    fun getOutputIndicatorsDetails(): Set<IndicatorOutputDto>

    fun getOutputIndicatorsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<IndicatorOutputDto>

    fun existsOutputByIdentifier(identifier: String): Boolean

    fun save(indicator: InputIndicatorOutputCreate): OutputIndicatorOutput

    fun save(indicator: InputIndicatorOutputUpdate): OutputIndicatorOutput
    //endregion

    //region INDICATOR RESULT

    fun getResultIndicatorById(id: Long): OutputIndicatorResult

    fun getResultIndicators(pageable: Pageable): Page<OutputIndicatorResult>

    fun existsResultByIdentifier(identifier: String): Boolean

    fun save(indicator: InputIndicatorResultCreate): OutputIndicatorResult

    fun save(indicator: InputIndicatorResultUpdate): OutputIndicatorResult
    //endregion

}
