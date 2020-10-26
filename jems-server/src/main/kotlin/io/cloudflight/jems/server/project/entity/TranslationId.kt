package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.SystemLanguage
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

/**
 * ID for the use to embed the PK for translation tables.
 */
@Embeddable
data class TranslationId(

    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val language: SystemLanguage

) : Serializable
