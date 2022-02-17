package io.cloudflight.jems.server.programme.service.listProgrammeDataExports

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.exportMetaData
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ListProgrammeDataExportsTest : UnitTest() {

    @MockK
    lateinit var persistence: ProgrammeDataPersistence

    @InjectMockKs
    lateinit var listProgrammeDataExports: ListProgrammeDataExports

    @Test
    fun `should return list of programme data export metadata`() {
        val metadata = exportMetaData()
        every { persistence.listExportMetadata() } returns listOf(metadata)
        assertThat(listProgrammeDataExports.list()).containsExactly(metadata)
    }
}
