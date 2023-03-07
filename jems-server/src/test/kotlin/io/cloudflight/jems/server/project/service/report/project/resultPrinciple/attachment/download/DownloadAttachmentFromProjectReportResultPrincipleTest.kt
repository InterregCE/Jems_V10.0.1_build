package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.download

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadAttachmentFromProjectReportResultPrincipleTest {

    companion object {
        fun resultPrinciple(projectResult: ProjectReportProjectResult) = ProjectReportResultPrinciple(
            projectResults = listOf(projectResult),
            horizontalPrinciples = mockk(),
            sustainableDevelopmentDescription = emptySet(),
            equalOpportunitiesDescription = emptySet(),
            sexualEqualityDescription = emptySet(),
        )
    }

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @MockK
    private lateinit var resultPrinciplePersistence: ProjectReportResultPrinciplePersistence

    @InjectMockKs
    private lateinit var interactor: DownloadAttachmentFromProjectReportResultPrinciple

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, resultPrinciplePersistence)
    }

    @Test
    fun download() {
        val projectId = 31L
        val reportId = 32L
        val number = 33
        val fileId = 34L
        val projectResult = mockk<ProjectReportProjectResult> {
            every { resultNumber } returns number
            every { attachment?.id } returns fileId
        }
        val download = mockk<Pair<String, ByteArray>>()

        every { resultPrinciplePersistence.getProjectResultPrinciples(projectId, reportId) }.returns(resultPrinciple(projectResult))
        every { filePersistence.existsFile(JemsFileType.ProjectResult, fileId) } returns true
        every { filePersistence.downloadFile(JemsFileType.ProjectResult, fileId) } returns download

        Assertions.assertThat(interactor.download(projectId, reportId, number)).isEqualTo(download)
    }

    @Test
    fun downloadFails() {
        val projectId = 41L
        val reportId = 42L
        val number = 43
        val projectResult = mockk<ProjectReportProjectResult> {
            every { resultNumber } returns number
            every { attachment } returns null
        }

        every { resultPrinciplePersistence.getProjectResultPrinciples(projectId, reportId) }.returns(resultPrinciple(projectResult))

        assertThrows<FileNotFound> { interactor.download(projectId, reportId, number) }
    }
}
