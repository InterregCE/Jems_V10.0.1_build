package io.cloudflight.jems.server.programme.service.exportProgrammeData

import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.EXPORT_LANGUAGE
import io.cloudflight.jems.server.programme.service.EXPORT_LANGUAGE_DATA
import io.cloudflight.jems.server.programme.service.INPUT_LANGUAGE
import io.cloudflight.jems.server.programme.service.INPUT_LANGUAGE_DATA
import io.cloudflight.jems.server.programme.service.PLUGIN_KEY
import io.cloudflight.jems.server.programme.service.exportMetaData
import io.cloudflight.jems.server.programme.service.exportResult
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

internal class ExportProgrammeDataServiceTest : UnitTest() {

    @MockK
    lateinit var programmeDataPersistence: ProgrammeDataPersistence

    @InjectMockKs
    lateinit var exportProgrammeDataService: ExportProgrammeDataService

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class Execute {
        @Test
        fun `should execute export programme data plugin and save the result file and update it's metadata after export is done`() {
            val pluginMockk = mockk<ProgrammeDataExportPlugin>()
            val exportEndedAt = ZonedDateTime.now()
            val exportResult = exportResult(exportEndedAt = exportEndedAt)

            every { pluginMockk.export(EXPORT_LANGUAGE_DATA, INPUT_LANGUAGE_DATA) } returns exportResult
            every { pluginMockk.getKey() } returns PLUGIN_KEY
            every { programmeDataPersistence.saveExportFile(PLUGIN_KEY, exportResult.content, true) } returns Unit
            every {
                programmeDataPersistence.updateExportMetaData(
                    PLUGIN_KEY, exportResult.fileName, exportResult.contentType,
                    exportResult.startTime, exportResult.endTime!!
                )
            } returns exportMetaData()

            exportProgrammeDataService.execute(pluginMockk, EXPORT_LANGUAGE, INPUT_LANGUAGE)

            verify {
                programmeDataPersistence.saveExportFile(PLUGIN_KEY, exportResult.content, true)

                programmeDataPersistence.updateExportMetaData(
                    PLUGIN_KEY, exportResult.fileName, exportResult.contentType,
                    exportResult.startTime, exportResult.endTime!!
                )
            }
            verify(exactly = 0) { programmeDataPersistence.deleteExportMetaData(PLUGIN_KEY) }
        }


        @Test
        fun `should update end time of export file meta data if there is any problem in executing export on the plugin`() {
            val pluginMockk = mockk<ProgrammeDataExportPlugin>()
            val endTimeSlot = slot<ZonedDateTime>()

            every { pluginMockk.getKey() } returns PLUGIN_KEY
            every { pluginMockk.export(EXPORT_LANGUAGE_DATA, INPUT_LANGUAGE_DATA) } throws RuntimeException()
            every { programmeDataPersistence.updateExportMetaData(PLUGIN_KEY, null, null, null, capture(endTimeSlot)) } returns exportMetaData()

            exportProgrammeDataService.execute(pluginMockk, EXPORT_LANGUAGE, INPUT_LANGUAGE)

            verify {
                programmeDataPersistence.updateExportMetaData(PLUGIN_KEY, null, null, null, endTimeSlot.captured)
            }
        }

        @Test
        fun `should update end time of export file meta data if there is any problem in saving file content`() {
            val pluginMockk = mockk<ProgrammeDataExportPlugin>()
            val exportEndedAt = ZonedDateTime.now()
            val exportResult = exportResult(exportEndedAt = exportEndedAt)
            val endTimeSlot = slot<ZonedDateTime>()

            every { pluginMockk.export(EXPORT_LANGUAGE_DATA, INPUT_LANGUAGE_DATA) } returns exportResult
            every { pluginMockk.getKey() } returns PLUGIN_KEY
            every {
                programmeDataPersistence.saveExportFile(PLUGIN_KEY, exportResult.content, true)
            } throws RuntimeException()
            every { programmeDataPersistence.updateExportMetaData(PLUGIN_KEY, null, null, null, capture(endTimeSlot) ) } returns exportMetaData(
                PLUGIN_KEY)

            exportProgrammeDataService.execute(pluginMockk, EXPORT_LANGUAGE, INPUT_LANGUAGE)

            verifyOrder {
                programmeDataPersistence.saveExportFile(PLUGIN_KEY, exportResult.content, true)
                programmeDataPersistence.updateExportMetaData(PLUGIN_KEY, null, null , null, capture(endTimeSlot))
            }
        }

        @Test
        fun `should update end time of export file meta data if there is any problem in updating file metadata`() {
            val pluginMockk = mockk<ProgrammeDataExportPlugin>()
            val exportEndedAt = ZonedDateTime.now()
            val exportResult = exportResult(exportEndedAt = exportEndedAt)
            val endTimeSlot = slot<ZonedDateTime>()

            every { pluginMockk.export(EXPORT_LANGUAGE_DATA, INPUT_LANGUAGE_DATA) } returns exportResult
            every { pluginMockk.getKey() } returns PLUGIN_KEY
            every { programmeDataPersistence.saveExportFile(PLUGIN_KEY, exportResult.content, true) } returns Unit
            every { programmeDataPersistence.updateExportMetaData(PLUGIN_KEY, null, null, null, capture(endTimeSlot)) } returns exportMetaData(
                PLUGIN_KEY)
            every {
                programmeDataPersistence.updateExportMetaData(
                    PLUGIN_KEY, exportResult.fileName, exportResult.contentType,
                    exportResult.startTime, exportResult.endTime!!
                )
            } throws RuntimeException()

            exportProgrammeDataService.execute(pluginMockk, EXPORT_LANGUAGE, INPUT_LANGUAGE)

            verifyOrder {
                programmeDataPersistence.saveExportFile(PLUGIN_KEY, exportResult.content, true)
                programmeDataPersistence.updateExportMetaData(PLUGIN_KEY, null, null, null, endTimeSlot.captured)
            }
        }
    }

    @Nested
    inner class SaveExportFileMetaData {

        @Test
        fun `should save export file metadata when there in no export in progress`() {
            every { programmeDataPersistence.listExportMetadata() } returns listOf(exportMetaData(exportEndedAt = ZonedDateTime.now()))
            every { programmeDataPersistence.deleteExportMetaData(PLUGIN_KEY) } returns Unit
            every {
                programmeDataPersistence.saveExportMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE, any())
            } returns exportMetaData()

            exportProgrammeDataService.saveExportFileMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)

            verify { programmeDataPersistence.saveExportMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE, any()) }
        }

        @Test
        fun `should throw ExportInProgressException when there is an export that is not done yet and it's not timed out`() {
            every { programmeDataPersistence.listExportMetadata() } returns listOf(
                exportMetaData(
                    requestTime = ZonedDateTime.now().minusMinutes(EXPORT_TIMEOUT_IN_MINUTES).plusMinutes(1)
                )
            )
            assertThrows<ExportInProgressException> {
                exportProgrammeDataService.saveExportFileMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)
            }
        }

        @Test
        fun `should not throw ExportInProgressException when export is timed out`() {
            every { programmeDataPersistence.listExportMetadata() } returns listOf(
                exportMetaData(requestTime = ZonedDateTime.now().minusMinutes(EXPORT_TIMEOUT_IN_MINUTES))
            )
            every { programmeDataPersistence.deleteExportMetaData(PLUGIN_KEY) } returns Unit
            every {
                programmeDataPersistence.saveExportMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE, any())
            } returns exportMetaData()
            assertDoesNotThrow {
                exportProgrammeDataService.saveExportFileMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)
            }
        }

        @Test
        fun `should not delete export metadata when metadata with current plugin key does not exist`() {
            every { programmeDataPersistence.listExportMetadata() } returns listOf(
                exportMetaData(exportEndedAt = ZonedDateTime.now(), pluginKey = "another-plugin-key")
            )
            every {
                programmeDataPersistence.saveExportMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE, any())
            } returns exportMetaData()
            exportProgrammeDataService.saveExportFileMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)

            verify(exactly = 0) { programmeDataPersistence.deleteExportMetaData(any()) }
        }

    }
}
