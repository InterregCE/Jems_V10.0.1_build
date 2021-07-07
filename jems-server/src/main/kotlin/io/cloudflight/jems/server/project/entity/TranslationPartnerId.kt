package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

/**
 * ID for the use to embed the PK for translation tables for Partner relations.
 */
@Embeddable
class TranslationPartnerId(

    @Column(name = "partner_id")
    @field:NotNull
    val partnerId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is TranslationPartnerId && partnerId == other.partnerId && language == other.language

    override fun hashCode(): Int = Objects.hash(partnerId, language)

}
