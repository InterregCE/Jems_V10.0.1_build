package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import java.time.ZonedDateTime

data class ProjectCallSettingsDTO(
    val callId: Long,
    val callName: String,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val endDateStep1: ZonedDateTime?,
    val lengthOfPeriod: Int,
    val isAdditionalFundAllowed: Boolean,
    val flatRates: FlatRateSetupDTO,
    val lumpSums: List<ProgrammeLumpSumDTO>,
    val unitCosts: List<ProgrammeUnitCostDTO>,
    var applicationFormConfiguration: ApplicationFormConfigurationDTO

)
