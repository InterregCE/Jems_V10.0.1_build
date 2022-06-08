package io.cloudflight.jems.server.call.entity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ApplicationFormFieldConfigurationId(

    @Column
    @field:NotNull
    val id: String,

    @field:NotNull
    @JoinColumn(name = "call_id", referencedColumnName = "id")
    @ManyToOne
    var callEntity: CallEntity

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ApplicationFormFieldConfigurationId && id == other.id && callEntity == other.callEntity

    override fun hashCode(): Int = Objects.hash(id, callEntity)

}
