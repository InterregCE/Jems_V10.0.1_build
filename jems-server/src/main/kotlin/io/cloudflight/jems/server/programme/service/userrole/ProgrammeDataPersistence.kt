package io.cloudflight.jems.server.programme.service.userrole

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import java.time.ZonedDateTime

interface ProgrammeDataPersistence {

    fun getProgrammeData(): ProgrammeData
    fun getProgrammeName(): String?
    fun getDefaultUserRole(): Long?
    fun updateDefaultUserRole(userRoleId: Long)

    fun listExportMetadata(): List<ProgrammeDataExportMetadata>
    fun saveExportMetaData(
        pluginKey: String, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, requestTime: ZonedDateTime
    ): ProgrammeDataExportMetadata
    fun updateExportMetaData(
        pluginKey: String, fileName: String, contentType: String, startTime: ZonedDateTime?, endTime: ZonedDateTime
    ): ProgrammeDataExportMetadata
    fun deleteExportMetaData(pluginKey: String)
    fun getExportMetaData(pluginKey: String): ProgrammeDataExportMetadata
    fun getExportFile(pluginKey: String): ByteArray
    fun saveExportFile(pluginKey: String, content: ByteArray, overwriteIfExists: Boolean = true)
}
