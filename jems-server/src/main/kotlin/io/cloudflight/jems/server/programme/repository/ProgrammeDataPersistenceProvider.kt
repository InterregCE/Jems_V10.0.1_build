package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeDataExportMetadataEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

const val PROGRAMME_DATA_EXPORT_BUCKET = "programme-data-exports"

@Repository
class ProgrammeDataPersistenceProvider(
    private val programmeDataRepository: ProgrammeDataRepository,
    private val programmeDataExportMetaDataRepository: ProgrammeDataExportMetaDataRepository,
    private val minioStorage: MinioStorage
) : ProgrammeDataPersistence {

    companion object {
        const val programmeDataId = 1L
    }

    @Transactional(readOnly = true)
    override fun getProgrammeData(): ProgrammeData =
        getProgrammeDataOrThrow().toModel()

    @Transactional(readOnly = true)
    override fun getProgrammeName(): String? =
        getProgrammeDataOrThrow().title

    @Transactional(readOnly = true)
    override fun getDefaultUserRole(): Long? {
        return getProgrammeDataOrThrow().defaultUserRoleId
    }

    @Transactional
    override fun updateDefaultUserRole(userRoleId: Long) {
        val programmeData = getProgrammeDataOrThrow()
        programmeDataRepository.save(programmeData.copy(defaultUserRoleId = userRoleId))
    }

    @Transactional(readOnly = true)
    override fun listExportMetadata() =
        programmeDataExportMetaDataRepository.findAllByOrderByRequestTimeDesc().toModel()

    @Transactional
    override fun saveExportMetaData(
        pluginKey: String, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, requestTime: ZonedDateTime
    ) =
        programmeDataExportMetaDataRepository.save(
            ProgrammeDataExportMetadataEntity(
                pluginKey = pluginKey,
                exportLanguage = exportLanguage,
                inputLanguage = inputLanguage,
                requestTime = requestTime
            )
        ).toModel()

    @Transactional
    override fun updateExportMetaData(
        pluginKey: String, fileName: String, contentType: String, startTime: ZonedDateTime?, endTime: ZonedDateTime
    ) =
        getProgrammeDataExportMetadataOrThrow(pluginKey).apply {
            this.fileName = fileName
            this.contentType = contentType
            this.exportStartedAt = startTime
            this.exportEndedAt = endTime
        }.toModel()

    @Transactional
    override fun deleteExportMetaData(pluginKey: String) =
        programmeDataExportMetaDataRepository.deleteById(pluginKey)

    @Transactional(readOnly = true)
    override fun getExportMetaData(pluginKey: String): ProgrammeDataExportMetadata =
        getProgrammeDataExportMetadataOrThrow(pluginKey).toModel()

    override fun getExportFile(pluginKey: String): ByteArray =
        minioStorage.getFile(PROGRAMME_DATA_EXPORT_BUCKET, pluginKey)

    override fun saveExportFile(pluginKey: String, content: ByteArray, overwriteIfExists: Boolean) {
        minioStorage.saveFile(
            PROGRAMME_DATA_EXPORT_BUCKET, pluginKey, content.size.toLong(), content.inputStream(), overwriteIfExists
        )
    }

    private fun getProgrammeDataOrThrow(): ProgrammeDataEntity =
        programmeDataRepository.findById(programmeDataId).orElseThrow { ResourceNotFoundException("programmeData") }

    private fun getProgrammeDataExportMetadataOrThrow(pluginKey: String): ProgrammeDataExportMetadataEntity =
        programmeDataExportMetaDataRepository.findById(pluginKey).orElseThrow {
            ProgrammeExportMetaDataNotFoundException()
        }
}
