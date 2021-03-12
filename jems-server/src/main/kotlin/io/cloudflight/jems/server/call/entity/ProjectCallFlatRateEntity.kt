package io.cloudflight.jems.server.call.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_call_flat_rate")
class ProjectCallFlatRateEntity(

    @EmbeddedId
    val setupId: FlatRateSetupId,

    @field:NotNull
    var rate: Int,

    @field:NotNull
    var isAdjustable: Boolean

) {
    override fun equals(other: Any?): Boolean = this === other
        || other is ProjectCallFlatRateEntity
        && setupId == other.setupId
        && rate == other.rate
        && isAdjustable == other.isAdjustable

    override fun hashCode() = setupId.hashCode()

}
