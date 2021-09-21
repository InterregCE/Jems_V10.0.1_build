package io.cloudflight.jems.server.project.entity.partner.cofinancing

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class ProjectPartnerCoFinancingFundId(

    @Column
    @field:NotNull
    val partnerId: Long,

    @Column
    @field:NotNull
    val orderNr: Int

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectPartnerCoFinancingFundId && partnerId == other.partnerId && orderNr == other.orderNr

    override fun hashCode(): Int = Objects.hash(partnerId, orderNr)

}
