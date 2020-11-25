package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OfficeOnOther
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OfficeOnStaff
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OtherOnStaff
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.StaffCost
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.TravelOnStaff
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import java.util.stream.Collectors
import java.util.stream.Stream

fun FlatRateSetupDTO.toModel(): Set<ProjectCallFlatRate> =
    Stream.of(
        Pair(StaffCost, staffCostFlatRateSetup),
        Pair(OfficeOnStaff, officeOnStaffFlatRateSetup),
        Pair(OfficeOnOther, officeOnOtherFlatRateSetup),
        Pair(TravelOnStaff, travelOnStaffFlatRateSetup),
        Pair(OtherOnStaff, otherOnStaffFlatRateSetup),
    )
        .filter { it.second != null }
        .map { ProjectCallFlatRate(
            type = it.first,
            rate = it.second!!.rate,
            isAdjustable = it.second!!.isAdjustable
        ) }
        .collect(Collectors.toSet())

fun ProjectCallFlatRate.toDto() = FlatRateDTO(
    rate = rate,
    isAdjustable = isAdjustable,
)

fun Set<ProjectCallFlatRate>.toDto(): FlatRateSetupDTO {
    val groupedByType = associateBy { it.type }
    return FlatRateSetupDTO(
        staffCostFlatRateSetup = groupedByType[StaffCost]?.toDto(),
        officeOnStaffFlatRateSetup = groupedByType[OfficeOnStaff]?.toDto(),
        officeOnOtherFlatRateSetup = groupedByType[OfficeOnOther]?.toDto(),
        travelOnStaffFlatRateSetup = groupedByType[TravelOnStaff]?.toDto(),
        otherOnStaffFlatRateSetup = groupedByType[OtherOnStaff]?.toDto(),
    )
}
