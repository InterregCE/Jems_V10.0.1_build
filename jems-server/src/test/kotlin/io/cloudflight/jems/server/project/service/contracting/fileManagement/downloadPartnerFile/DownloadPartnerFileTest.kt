package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
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
        private const val PARTNER_ID = 10L
        private const val FILE_ID = 20L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence


    @InjectMockKs
    lateinit var interactor: DownloadPartnerFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
    }

    @Test
    fun `download partner file`() {
        val file = mockk<Pair<String, ByteArray>>()
        every { reportFilePersistence.getFileTypeByPartnerId(FILE_ID, PARTNER_ID) } returns ProjectPartnerReportFileType.ContractPartnerDoc
        every { contractingFilePersistence.downloadFileByPartnerId(PARTNER_ID, FILE_ID) } returns file
        Assertions.assertThat(interactor.downloadPartnerFile(partnerId = PARTNER_ID, fileId = FILE_ID)).isEqualTo(file)
    }

}

