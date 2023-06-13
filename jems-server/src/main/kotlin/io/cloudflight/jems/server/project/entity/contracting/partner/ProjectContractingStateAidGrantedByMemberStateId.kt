package io.cloudflight.jems.server.project.entity.contracting.partner

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class ProjectContractingStateAidGrantedByMemberStateId(

    @Column
    @field:NotNull
    val partnerId: Long,

    @field:NotNull
    val countryCode: String

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectContractingStateAidGrantedByMemberStateId && partnerId == other.partnerId && countryCode == other.countryCode

    override fun hashCode(): Int = Objects.hash(partnerId, countryCode)

}
