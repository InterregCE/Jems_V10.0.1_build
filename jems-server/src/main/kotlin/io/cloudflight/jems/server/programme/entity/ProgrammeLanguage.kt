package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

/**
 * programme_language as language is a reserved sql:2016 keyword.
 * ui contains the interface/system language selection.
 * fallback should be the default language.
 */
@Entity(name = "programme_language")
data class ProgrammeLanguage(

    @Id
    @Enumerated(EnumType.STRING)
    val code: SystemLanguage,

    @Column(nullable = false)
    val ui: Boolean = false,

    @Column(nullable = false)
    val fallback: Boolean = false,

    @Column(nullable = false)
    val input: Boolean = false

)
