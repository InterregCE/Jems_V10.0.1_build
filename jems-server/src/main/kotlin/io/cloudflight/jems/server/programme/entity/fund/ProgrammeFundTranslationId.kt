package io.cloudflight.jems.server.programme.entity.fund

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
data class ProgrammeFundTranslationId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id")
    @field:NotNull
    val fund: ProgrammeFundEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable {

    override fun equals(other: Any?): Boolean = (other is ProgrammeFundTranslationId)
        && fund.id > 0
        && fund.id == other.fund.id
        && language == other.language

    override fun hashCode(): Int = if (fund.id > 0) fund.id.hashCode() else super.hashCode()

}
