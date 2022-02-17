package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "programme_data_export_metadata")
class ProgrammeDataExportMetadataEntity(
    @Id
    val pluginKey: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var exportLanguage: SystemLanguage,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var inputLanguage: SystemLanguage,

    var fileName: String? = null,

    var contentType: String? = null,

    @field:NotNull
    var requestTime: ZonedDateTime,

    var exportStartedAt: ZonedDateTime? = null,

    var exportEndedAt: ZonedDateTime? = null,
)
