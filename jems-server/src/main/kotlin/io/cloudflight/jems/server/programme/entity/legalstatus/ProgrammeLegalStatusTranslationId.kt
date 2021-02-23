package io.cloudflight.jems.server.programme.entity.legalstatus

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
data class ProgrammeLegalStatusTranslationId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_status_id")
    @field:NotNull
    val legalStatus: ProgrammeLegalStatusEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable {

    override fun equals(other: Any?): Boolean = (other is ProgrammeLegalStatusTranslationId)
        && legalStatus.id == other.legalStatus.id
        && language == other.language

    override fun hashCode(): Int = legalStatus.id.hashCode()

}
