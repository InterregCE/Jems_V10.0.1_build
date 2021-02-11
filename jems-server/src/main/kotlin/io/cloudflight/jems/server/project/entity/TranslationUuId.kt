package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

/**
 * ID for the use to embed the PK for translation tables for UUID relations.
 */
@Embeddable
data class TranslationUuId(

    @Column(name = "reference_id")
    @field:NotNull
    val id: UUID,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
