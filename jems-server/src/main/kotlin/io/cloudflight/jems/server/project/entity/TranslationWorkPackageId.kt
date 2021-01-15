package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class TranslationWorkPackageId(

    @field:NotNull
    val workPackageId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
