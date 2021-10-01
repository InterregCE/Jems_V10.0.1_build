package io.cloudflight.jems.server.call.entity

import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_call_fund")
class CallFundRateEntity(

    @EmbeddedId
    var setupId: FundSetupId,

    @field:NotNull
    var rate: BigDecimal,

    @field:NotNull
    var isAdjustable: Boolean

) {
    override fun equals(other: Any?): Boolean = this === other
        || other is CallFundRateEntity
        && setupId == other.setupId
        && rate == other.rate
        && isAdjustable == other.isAdjustable

    override fun hashCode() = setupId.hashCode()

}
