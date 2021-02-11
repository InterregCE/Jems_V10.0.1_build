package io.cloudflight.jems.server.programme.entity.language

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.validation.constraints.NotNull

/**
 * programme_language as language is a reserved sql:2016 keyword.
 * ui contains the interface/system language selection.
 * fallback should be the default language.
 */
@Entity(name = "programme_language")
data class ProgrammeLanguageEntity(

    @Id
    @Enumerated(EnumType.STRING)
    val code: SystemLanguage,

    @field:NotNull
    val ui: Boolean = false,

    @field:NotNull
    val fallback: Boolean = false,

    @field:NotNull
    val input: Boolean = false

)
