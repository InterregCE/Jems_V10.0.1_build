package io.cloudflight.jems.server.programme.entity.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class TranslationFileId(
    @Column
    @field:NotNull
    @Enumerated(EnumType.STRING)
    val language: SystemLanguage,

    @Column
    @field:NotNull
    @Enumerated(EnumType.STRING)
    val fileType: TranslationFileType
) : Serializable
