package io.cloudflight.jems.server.programme.entity.translation

import java.time.ZonedDateTime
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "translation_file")
data class TranslationFileEntity(

    @EmbeddedId
    @field:NotNull
    val id: TranslationFileId,

    @field:NotNull
    val lastModified: ZonedDateTime

)
