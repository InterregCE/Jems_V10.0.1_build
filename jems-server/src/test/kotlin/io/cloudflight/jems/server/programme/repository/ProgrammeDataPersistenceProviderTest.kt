package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.service.EXPORT_LANGUAGE
import io.cloudflight.jems.server.programme.service.INPUT_LANGUAGE
import io.cloudflight.jems.server.programme.service.PLUGIN_KEY
import io.cloudflight.jems.server.programme.service.exportMetaData
import io.cloudflight.jems.server.programme.service.exportMetaDataEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

class ProgrammeDataPersistenceProviderTest : UnitTest() {

    companion object {
        private const val programmeDataId = 1L

        private val programmeDataEntity = ProgrammeDataEntity(
            id = 1,
            cci = "cci",
            title = "title",
            version = "version",
            firstYear = 2010,
            lastYear = 2030,
            eligibleFrom = LocalDate.of(2020, 1, 1),
            eligibleUntil = LocalDate.of(2030, 1, 1),
            commissionDecisionNumber = "",
            commissionDecisionDate = LocalDate.of(2020, 1, 1),
            programmeAmendingDecisionNumber = "",
            programmeAmendingDecisionDate = LocalDate.of(2020, 1, 1),
            projectIdProgrammeAbbreviation = "NL-DE_",
            projectIdUseCallId = true,
            defaultUserRoleId = 1L
        )

        private val programmeData = ProgrammeData(
            id = 1,
            cci = "cci",
            title = "title",
            version = "version",
            firstYear = 2010,
            lastYear = 2030,
            eligibleFrom = LocalDate.of(2020, 1, 1),
            eligibleUntil = LocalDate.of(2030, 1, 1),
            commissionDecisionNumber = "",
            commissionDecisionDate = LocalDate.of(2020, 1, 1),
            programmeAmendingDecisionNumber = "",
            programmeAmendingDecisionDate = LocalDate.of(2020, 1, 1),
            projectIdProgrammeAbbreviation = "NL-DE_",
            projectIdUseCallId = true,
            defaultUserRoleId = 1L
        )
    }

    @MockK
    lateinit var repository: ProgrammeDataRepository

    @MockK
    lateinit var programmeDataExportMetaDataRepository: ProgrammeDataExportMetaDataRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @InjectMockKs
    lateinit var persistence: ProgrammeDataPersistenceProvider

    @Test
    fun `should return programme data when there is no problem`() {
        every { repository.findById(programmeDataId) } returns Optional.of(programmeDataEntity)
        assertThat(persistence.getProgrammeData()).isEqualTo(programmeData)
    }

    @Test
    fun getDefaultUserRole() {
        every { repository.findById(programmeDataId) } returns Optional.of(programmeDataEntity)
        assertThat(persistence.getDefaultUserRole()).isEqualTo(1L)
    }

    @Test
    fun getDefaultUserRoleNotFoundException() {
        every { repository.findById(programmeDataId) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { persistence.getDefaultUserRole() }
    }

    @Test
    fun updateDefaultUserRole() {
        every { repository.findById(programmeDataId) } returns Optional.of(programmeDataEntity)
        val dataToSave = slot<ProgrammeDataEntity>()
        every { repository.save(capture(dataToSave)) } returnsArgument 0

        persistence.updateDefaultUserRole(2L)
        assertThat(dataToSave.captured).isEqualTo(
            programmeDataEntity.copy(defaultUserRoleId = 2L)
        )
    }

    @Test
    fun `should return list of export meta data`() {
        val requestTime = ZonedDateTime.now()
        val exportEndedAt = ZonedDateTime.now()
        val exportStartedAt = ZonedDateTime.now()
        val fileName = "fileName"
        every { programmeDataExportMetaDataRepository.findAllByOrderByRequestTimeDesc() } returns listOf(
            exportMetaDataEntity(
                fileName = fileName, exportEndedAt = exportEndedAt,
                exportStartedAt = exportStartedAt, requestTime = requestTime
            )
        )
        assertThat(persistence.listExportMetadata()).containsExactly(
            exportMetaData(
                fileName = fileName, exportEndedAt = exportEndedAt,
                exportStartedAt = exportStartedAt, requestTime = requestTime
            )
        )
    }

    @Test
    fun `should save export meta data`() {
        val requestTime = ZonedDateTime.now()
        every { programmeDataExportMetaDataRepository.save(any()) } returnsArgument 0
        assertThat(persistence.saveExportMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE, requestTime)).isEqualTo(
            exportMetaData(requestTime = requestTime)
        )
    }

    @Test
    fun `should update export meta data`() {
        val exportEndedAt = ZonedDateTime.now()
        val exportStartedAt = ZonedDateTime.now()
        val fileName = "fileName"
        val contentType = "contentType"
        val entity = exportMetaDataEntity(
            fileName = fileName, contentType = contentType,
            exportEndedAt = exportEndedAt, exportStartedAt = exportStartedAt
        )

        every { programmeDataExportMetaDataRepository.findById(PLUGIN_KEY) } returns Optional.of(entity)
        every { programmeDataExportMetaDataRepository.save(entity) } returnsArgument 0
        assertThat(persistence.updateExportMetaData(PLUGIN_KEY, fileName, contentType, exportStartedAt, exportEndedAt))
            .isEqualTo(
                exportMetaData(
                    fileName = fileName, contentType = contentType,
                    exportStartedAt = exportStartedAt, exportEndedAt = exportEndedAt
                )
            )
    }

    @Test
    fun `should throw ProgrammeExportMetaDataNotFoundException when updating meta data while it does not exist`() {
        every { programmeDataExportMetaDataRepository.findById(PLUGIN_KEY) } returns Optional.empty()
        assertThrows<ProgrammeExportMetaDataNotFoundException> {
            persistence.updateExportMetaData(PLUGIN_KEY, "filename", "content-type", null, ZonedDateTime.now())
        }
    }

    @Test
    fun `should delete export meta data`() {
        every { programmeDataExportMetaDataRepository.deleteById(PLUGIN_KEY) } returns Unit
        persistence.deleteExportMetaData(PLUGIN_KEY)
        verify { programmeDataExportMetaDataRepository.deleteById(PLUGIN_KEY) }
    }

    @Test
    fun `should return export meta data`() {
        val entity = exportMetaDataEntity()
        every { programmeDataExportMetaDataRepository.findById(PLUGIN_KEY) } returns Optional.of(entity)
        assertThat(persistence.getExportMetaData(PLUGIN_KEY)).isEqualTo(
            exportMetaData()
        )
    }

    @Test
    fun `should return export file`() {
        val byteArray = byteArrayOf(20)
        every { minioStorage.getFile(PROGRAMME_DATA_EXPORT_BUCKET, PLUGIN_KEY) } returns byteArray
        assertThat(persistence.getExportFile(PLUGIN_KEY)).isEqualTo(byteArray)
    }

    @Test
    fun `should save export file`() {
        val byteArray = byteArrayOf(20)
        val inputStreamSlot = slot<InputStream>()
        every {
            minioStorage.saveFile(
                PROGRAMME_DATA_EXPORT_BUCKET, PLUGIN_KEY, byteArray.size.toLong(), capture(inputStreamSlot), true
            )
        } returns Unit
        persistence.saveExportFile(PLUGIN_KEY, byteArray, true)
        verify {
            minioStorage.saveFile(
                PROGRAMME_DATA_EXPORT_BUCKET, PLUGIN_KEY, byteArray.size.toLong(), inputStreamSlot.captured, true
            )
        }
    }

    @Test
    fun `should throw ProgrammeExportMetaDataNotFoundException when getting meta data while it does not exist`() {
        every { programmeDataExportMetaDataRepository.findById(PLUGIN_KEY) } returns Optional.empty()
        assertThrows<ProgrammeExportMetaDataNotFoundException> {
            persistence.getExportMetaData(PLUGIN_KEY)
        }
    }

}
