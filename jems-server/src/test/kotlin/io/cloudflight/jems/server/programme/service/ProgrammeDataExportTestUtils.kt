package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.programme.entity.ProgrammeDataExportMetadataEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import java.time.ZoneOffset
import java.time.ZonedDateTime


const val PLUGIN_KEY = "plugin-key"
val EXPORT_LANGUAGE = SystemLanguage.EN
val INPUT_LANGUAGE = SystemLanguage.DE
val EXPORT_LANGUAGE_DATA = SystemLanguageData.EN
val INPUT_LANGUAGE_DATA = SystemLanguageData.DE

fun exportMetaDataEntity(
    pluginKey: String? = PLUGIN_KEY, fileName: String? = null, contentType: String? = null,
    exportLanguage: SystemLanguage = SystemLanguage.EN, inputLanguage: SystemLanguage = SystemLanguage.DE,
    requestTime: ZonedDateTime = ZonedDateTime.of(2022, 2,12, 10,11,12,13, ZoneOffset.UTC),
    exportStartedAt: ZonedDateTime? = null, exportEndedAt: ZonedDateTime? = null
) =
    ProgrammeDataExportMetadataEntity(
        pluginKey!!, exportLanguage, inputLanguage,
        fileName, contentType, requestTime, exportStartedAt, exportEndedAt
    )

fun exportMetaData(
    pluginKey: String? = PLUGIN_KEY, fileName: String? = null, contentType: String? = null,
    exportLanguage: SystemLanguage = SystemLanguage.EN, inputLanguage: SystemLanguage = SystemLanguage.DE,
    requestTime: ZonedDateTime = ZonedDateTime.of(2022, 2,12, 10,11,12,13, ZoneOffset.UTC),
    exportStartedAt: ZonedDateTime? = null, exportEndedAt: ZonedDateTime? = null
) =
    ProgrammeDataExportMetadata(
        pluginKey!!, fileName, contentType, exportLanguage, inputLanguage,
        requestTime, exportStartedAt, exportEndedAt
    )

fun exportResult(
    contentType: String = "content-type", fileName: String = "fileName", content: ByteArray = byteArrayOf(),
    exportStartedAt: ZonedDateTime? = null, exportEndedAt: ZonedDateTime? = null
) =
    ExportResult(contentType, fileName, content, exportStartedAt, exportEndedAt)
