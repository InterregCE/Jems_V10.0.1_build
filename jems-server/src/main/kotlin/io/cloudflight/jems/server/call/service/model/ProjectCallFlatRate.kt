package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType

data class ProjectCallFlatRate (
    val type: FlatRateType,
    val rate: Int,
    val adjustable: Boolean
): Comparable<ProjectCallFlatRate> {

    override fun compareTo(other: ProjectCallFlatRate): Int = type.compareTo(other.type)

}
