package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.entity.result.ProjectResultId
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

/**
 * ID for the use to embed the PK for translation tables for Project Results relations.
 */
@Embeddable
data class TranslationResultId(

    @field:NotNull
    val resultId: ProjectResultId,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
