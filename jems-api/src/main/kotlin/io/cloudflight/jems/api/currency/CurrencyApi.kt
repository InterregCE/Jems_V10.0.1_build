package io.cloudflight.jems.api.currency

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Optional

@Api("Currency Import and Conversion")
interface CurrencyApi {

    companion object {
        private const val ENDPOINT_API_CURRENCY = "/api/currency"
    }

    @ApiOperation("Retrieve info about currencies in the system for the specified month or current")
    @GetMapping(ENDPOINT_API_CURRENCY)
    fun getCurrencyRates(
        @RequestParam(required = false) year: Optional<Int>,
        @RequestParam(required = false) month: Optional<Int>
    ): List<CurrencyDTO>

    @ApiOperation("Import info about currencies into the system for the specified month or current")
    @PutMapping(ENDPOINT_API_CURRENCY)
    fun fetchCurrencyRates(
        @RequestParam(required = false) year: Optional<Int>,
        @RequestParam(required = false) month: Optional<Int>
    ): List<CurrencyDTO>

    @ApiOperation("Get a specific currency rate for date and Nut region")
    @GetMapping("$ENDPOINT_API_CURRENCY/{country}/conversion")
    fun getCurrencyRateForNutsRegion(
        @PathVariable country: String,
        @RequestParam(required = false) year: Optional<Int>,
        @RequestParam(required = false) month: Optional<Int>
    ): CurrencyDTO?

}
