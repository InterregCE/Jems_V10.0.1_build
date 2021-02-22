package io.cloudflight.jems.server.programme.entity.costoption

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class ProgrammeLumpSumTranslId(

    @field:NotNull
    val programmeLumpSumId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

): Serializable
