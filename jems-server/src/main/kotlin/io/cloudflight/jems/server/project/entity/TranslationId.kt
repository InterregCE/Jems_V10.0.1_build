package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

/**
 * ID for the use to embed the PK for translation tables for Project relations.
 */
@Embeddable
data class TranslationId(

    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
