package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumListDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZonedDateTime

data class CallDetailDTO(
    val id: Long,
    val name: String,
    val status: CallStatus,
    val startDateTime: ZonedDateTime,
    val endDateTimeStep1: ZonedDateTime?,
    val endDateTime: ZonedDateTime,
    val isAdditionalFundAllowed: Boolean,
    val lengthOfPeriod: Int?,
    val description: Set<InputTranslation> = emptySet(),
    val objectives: List<ProgrammePriorityDTO> = emptyList(),
    val strategies: List<ProgrammeStrategy> = emptyList(),
    val funds: List<ProgrammeFundDTO> = emptyList(),
    val flatRates: FlatRateSetupDTO,
    val lumpSums: List<ProgrammeLumpSumListDTO> = emptyList(),
    val unitCosts: List<ProgrammeUnitCostListDTO> = emptyList(),
    val applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfigurationDTO>
)
