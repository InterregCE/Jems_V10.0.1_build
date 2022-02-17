package io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.PLUGIN_KEY
import io.cloudflight.jems.server.programme.service.exportMetaData
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

internal class DownloadProgrammeDataExportFileTest : UnitTest() {
    @MockK
    lateinit var persistence: ProgrammeDataPersistence

    @InjectMockKs
    lateinit var downloadProgrammeDataExportFile: DownloadProgrammeDataExportFile

    @Test
    fun `should throw PluginKeyIsInvalidException when plugin key is null`() {
        assertThrows<PluginKeyIsInvalidException> {
            downloadProgrammeDataExportFile.download(null)
        }
    }

    @TestFactory
    fun `should throw ExportFileIsNotReadyException when exportation is not done yet`() =
        listOf(
            exportMetaData(fileName = " ", exportEndedAt = ZonedDateTime.now(), contentType = "content-type"),
            exportMetaData(fileName = null, exportEndedAt = ZonedDateTime.now(), contentType = "content-type"),
            exportMetaData(fileName = "fileName", exportEndedAt = null, contentType = "content-type"),
            exportMetaData(fileName = "fileName", exportEndedAt = ZonedDateTime.now(), contentType = " "),
            exportMetaData(fileName = "fileName", exportEndedAt = ZonedDateTime.now(), contentType = null),
        ).map { input ->
            DynamicTest.dynamicTest(
                "should throw ExportFileIsNotReadyException when exportation is not done yet"
            ) {
                every { persistence.getExportMetaData(PLUGIN_KEY) } returns input
                assertThrows<ExportFileIsNotReadyException> {
                    downloadProgrammeDataExportFile.download(PLUGIN_KEY)
                }
            }
        }

    @Test
    fun `should return ExportResult when there is no problem`() {
        val byteArray = "content".toByteArray()
        val startTime = ZonedDateTime.now()
        val endTime = ZonedDateTime.now().plusMinutes(2)
        val exportMetaData = exportMetaData(
            fileName = "exported-file.xlsx", contentType = "content-type",
            exportStartedAt = startTime, exportEndedAt = endTime
        )
        every { persistence.getExportMetaData(PLUGIN_KEY) } returns exportMetaData
        every { persistence.getExportFile(PLUGIN_KEY) } returns byteArray
        val result = downloadProgrammeDataExportFile.download(PLUGIN_KEY)
        assertThat(result.fileName).isEqualTo(exportMetaData.fileName!!)
        assertThat(result.contentType).isEqualTo(exportMetaData.contentType!!)
        assertThat(result.content).isEqualTo(byteArray)
        assertThat(result.endTime).isEqualTo(endTime)
        assertThat(result.startTime).isEqualTo(startTime)
    }
}
