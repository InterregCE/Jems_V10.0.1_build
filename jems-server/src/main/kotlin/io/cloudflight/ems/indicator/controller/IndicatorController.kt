package io.cloudflight.ems.indicator.controller

import io.cloudflight.ems.api.indicator.IndicatorApi
import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputUpdate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultUpdate
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorOutput
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorResult
import io.cloudflight.ems.indicator.service.IndicatorService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class IndicatorController(
    private val indicatorService: IndicatorService
) : IndicatorApi {

    //region INDICATOR OUTPUT

    override fun getAllIndicatorOutput(pageable: Pageable): Page<OutputIndicatorOutput> {
        return indicatorService.getOutputIndicators(pageable)
    }

    override fun getIndicatorOutput(id: Long): OutputIndicatorOutput {
        return indicatorService.getOutputIndicatorById(id)
    }

    override fun createIndicatorOutput(indicator: InputIndicatorOutputCreate): OutputIndicatorOutput {
        return indicatorService.save(indicator)
    }

    override fun updateIndicatorOutput(indicator: InputIndicatorOutputUpdate): OutputIndicatorOutput {
        return indicatorService.save(indicator)
    }
    // endregion

    //region INDICATOR RESULT

    override fun getAllIndicatorResult(pageable: Pageable): Page<OutputIndicatorResult> {
        return indicatorService.getResultIndicators(pageable)
    }

    override fun getIndicatorResult(id: Long): OutputIndicatorResult {
        return indicatorService.getResultIndicatorById(id)
    }

    override fun createIndicatorResult(indicator: InputIndicatorResultCreate): OutputIndicatorResult {
        return indicatorService.save(indicator)
    }

    override fun updateIndicatorResult(indicator: InputIndicatorResultUpdate): OutputIndicatorResult {
        return indicatorService.save(indicator)
    }
    //endregion

}
