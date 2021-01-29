package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OTHER_COSTS_ON_STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import java.util.stream.Collectors
import java.util.stream.Stream

fun FlatRateSetupDTO.toModel(): Set<ProjectCallFlatRate> =
    Stream.of(
        Pair(STAFF_COSTS, staffCostFlatRateSetup),
        Pair(OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, officeAndAdministrationOnStaffCostsFlatRateSetup),
        Pair(OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS, officeAndAdministrationOnDirectCostsFlatRateSetup),
        Pair(TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS, travelAndAccommodationOnStaffCostsFlatRateSetup),
        Pair(OTHER_COSTS_ON_STAFF_COSTS, otherCostsOnStaffCostsFlatRateSetup),
    )
        .filter { it.second != null }
        .map {
            ProjectCallFlatRate(
                type = it.first,
                rate = it.second!!.rate,
                isAdjustable = it.second!!.isAdjustable
            )
        }
        .collect(Collectors.toSet())

fun ProjectCallFlatRate.toDto() = FlatRateDTO(
    rate = rate,
    isAdjustable = isAdjustable,
)

fun Set<ProjectCallFlatRate>.toDto(): FlatRateSetupDTO {
    val groupedByType = associateBy { it.type }
    return FlatRateSetupDTO(
        staffCostFlatRateSetup = groupedByType[STAFF_COSTS]?.toDto(),
        officeAndAdministrationOnStaffCostsFlatRateSetup = groupedByType[OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS]?.toDto(),
        officeAndAdministrationOnDirectCostsFlatRateSetup = groupedByType[OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS]?.toDto(),
        travelAndAccommodationOnStaffCostsFlatRateSetup = groupedByType[TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS]?.toDto(),
        otherCostsOnStaffCostsFlatRateSetup = groupedByType[OTHER_COSTS_ON_STAFF_COSTS]?.toDto(),
    )
}
