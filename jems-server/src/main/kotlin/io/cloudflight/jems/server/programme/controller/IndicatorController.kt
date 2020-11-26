package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.IndicatorApi
import io.cloudflight.jems.api.programme.dto.indicator.IndicatorOutputDto
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputUpdate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultUpdate
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorOutput
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorResult
import io.cloudflight.jems.server.programme.service.indicator.IndicatorService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class IndicatorController(
    private val indicatorService: IndicatorService
) : IndicatorApi {

    //region INDICATOR OUTPUT

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun getAllIndicatorOutput(pageable: Pageable): Page<OutputIndicatorOutput> {
        return indicatorService.getOutputIndicators(pageable)
    }

    @PreAuthorize("@programmeSetupAuthorization.canReadIndicators()")
    override fun getAllIndicatorOutputDetail(): Set<IndicatorOutputDto> {
        return indicatorService.getOutputIndicatorsDetails()
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun getIndicatorOutput(id: Long): OutputIndicatorOutput {
        return indicatorService.getOutputIndicatorById(id)
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun createIndicatorOutput(indicator: InputIndicatorOutputCreate): OutputIndicatorOutput {
        return indicatorService.save(indicator)
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun updateIndicatorOutput(indicator: InputIndicatorOutputUpdate): OutputIndicatorOutput {
        return indicatorService.save(indicator)
    }
    // endregion

    //region INDICATOR RESULT

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun getAllIndicatorResult(pageable: Pageable): Page<OutputIndicatorResult> {
        return indicatorService.getResultIndicators(pageable)
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun getIndicatorResult(id: Long): OutputIndicatorResult {
        return indicatorService.getResultIndicatorById(id)
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun createIndicatorResult(indicator: InputIndicatorResultCreate): OutputIndicatorResult {
        return indicatorService.save(indicator)
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun updateIndicatorResult(indicator: InputIndicatorResultUpdate): OutputIndicatorResult {
        return indicatorService.save(indicator)
    }
    //endregion

}
