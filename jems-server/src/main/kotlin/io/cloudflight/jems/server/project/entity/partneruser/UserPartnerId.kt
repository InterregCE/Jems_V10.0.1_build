package io.cloudflight.jems.server.project.entity.partneruser

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class UserPartnerId(

    @field:NotNull
    @Column(name = "account_id")
    val userId: Long,

    @field:NotNull
    val partnerId: Long,

) : Serializable {

    override fun equals(other: Any?) = this === other ||
        other is UserPartnerId &&
        userId == other.userId &&
        partnerId == other.partnerId

    override fun hashCode() = Objects.hash(userId, partnerId)
}
