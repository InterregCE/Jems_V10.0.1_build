package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.delete

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteAttachmentFromProjectReportResultPrincipleTest {

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
    private lateinit var interactor: DeleteAttachmentFromProjectReportResultPrinciple

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, resultPrinciplePersistence)
    }

    @Test
    fun delete() {
        val projectId = 51L
        val reportId = 52L
        val resultNumber = 53
        val projectResult = mockk<ProjectReportProjectResult>()
        val fileId = 54L

        every { projectResult.resultNumber } returns resultNumber
        every { projectResult.attachment?.id } returns fileId
        every { resultPrinciplePersistence.getProjectResultPrinciples(projectId, reportId) }.returns(resultPrinciple(projectResult))
        every { filePersistence.existsFile(JemsFileType.ProjectResult, fileId) } returns true
        every { filePersistence.deleteFile(JemsFileType.ProjectResult, fileId) } returns Unit

        interactor.delete(projectId, reportId, resultNumber)

        verify { filePersistence.deleteFile(JemsFileType.ProjectResult, fileId) }
    }

    @Test
    fun deleteFails() {
        val projectId = 61L
        val reportId = 62L
        val resultNumber = 63
        val projectResult = mockk<ProjectReportProjectResult>()

        every { projectResult.resultNumber } returns resultNumber
        every { projectResult.attachment } returns null
        every { resultPrinciplePersistence.getProjectResultPrinciples(projectId, reportId) }.returns(resultPrinciple(projectResult))

        assertThrows<FileNotFound> { interactor.delete(projectId, reportId, resultNumber) }
    }
}
