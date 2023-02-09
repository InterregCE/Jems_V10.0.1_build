package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DownloadPartnerFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val FILE_ID = 20L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence


    @InjectMockKs
    lateinit var interactor: DownloadPartnerFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
    }

    @Test
    fun `download partner file`() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.getFileType(FILE_ID, PROJECT_ID) } returns JemsFileType.ContractPartnerDoc
        every { contractingFilePersistence.downloadFile(PROJECT_ID, FILE_ID) } returns file
        Assertions.assertThat(interactor.downloadPartnerFile(projectId = PROJECT_ID, fileId = FILE_ID)).isEqualTo(file)
    }
}
