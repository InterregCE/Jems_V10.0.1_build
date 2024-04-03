package io.cloudflight.jems.server.project.controller.report.project.resultPrinciple

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect.*
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportProjectResultDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportResultPrincipleDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.UpdateProjectReportProjectResultDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.UpdateProjectReportResultPrincipleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.delete.DeleteAttachmentFromProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.download.DownloadAttachmentFromProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.upload.UploadAttachmentToProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.getResultPrinciple.GetProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple.UpdateProjectReportResultPrincipleInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.ZonedDateTime

class ProjectReportResultPrincipleControllerTest : UnitTest() {

    companion object {

        val time = ZonedDateTime.now()

        fun resultPrinciple(projectResult: ProjectReportProjectResult) = ProjectReportResultPrinciple(
            listOf(projectResult),
            ProjectHorizontalPrinciples(PositiveEffects, NegativeEffects, Neutral),
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
            sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription")),
        )

        fun projectResult(resultNumber: Int) = ProjectReportProjectResult(
            resultNumber = resultNumber,
            programmeResultIndicatorId = 622L,
            programmeResultIndicatorIdentifier = "prog-res-indic",
            programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "test-en")),
            baseline = BigDecimal.valueOf(1),
            targetValue = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            previouslyReported = BigDecimal.valueOf(4),
            periodDetail = ProjectPeriod(4, 12, 24),
            description = setOf(InputTranslation(SystemLanguage.NL, "NL-desc")),
            measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "test-measure-EN")),
            attachment = JemsFileMetadata(697L, "file.att", time),
            deactivated = false
        )

        fun resultPrincipleDto(resultNumber: Int) = ProjectReportResultPrincipleDTO(
            projectResults = listOf(
                ProjectReportProjectResultDTO(
                    resultNumber = resultNumber,
                    programmeResultIndicatorId = 622L,
                    programmeResultIndicatorIdentifier = "prog-res-indic",
                    programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "test-en")),
                    baseline = BigDecimal.valueOf(1),
                    targetValue = BigDecimal.valueOf(2),
                    currentReport = BigDecimal.valueOf(3),
                    previouslyReported = BigDecimal.valueOf(4),
                    periodDetail = ProjectPeriodDTO(4, 12, 24, null, null),
                    description = setOf(InputTranslation(SystemLanguage.NL, "NL-desc")),
                    measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "test-measure-EN")),
                    attachment = JemsFileMetadataDTO(697L, name = "file.att", time),
                    deactivated = false
                )
            ),
            horizontalPrinciples = InputProjectHorizontalPrinciples(PositiveEffects, NegativeEffects, Neutral),
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
            sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription")),
        )

        fun updateResultPrincipleDto(resultNumber: Int) = UpdateProjectReportResultPrincipleDTO(
            projectResults = listOf(
                UpdateProjectReportProjectResultDTO(
                    resultNumber = resultNumber,
                    achievedInReportingPeriod = BigDecimal.valueOf(3),
                )
            ),
            sustainableDevelopmentDescription = setOf(),
            equalOpportunitiesDescription = setOf(),
            sexualEqualityDescription = setOf()
        )
    }

    @MockK
    private lateinit var getResultPrinciple: GetProjectReportResultPrincipleInteractor

    @MockK
    private lateinit var updateResultPrinciple: UpdateProjectReportResultPrincipleInteractor

    @MockK
    private lateinit var uploadAttachment: UploadAttachmentToProjectReportResultPrincipleInteractor

    @MockK
    private lateinit var downloadAttachment: DownloadAttachmentFromProjectReportResultPrincipleInteractor

    @MockK
    private lateinit var deleteAttachment: DeleteAttachmentFromProjectReportResultPrincipleInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportResultPrincipleController

    @BeforeEach()
    fun reset() {
        clearMocks(getResultPrinciple, updateResultPrinciple, uploadAttachment, downloadAttachment, deleteAttachment)
    }

    @Test
    fun get() {
        val projectId = 1L
        val reportId = 2L
        val resultNumber = 3

        every { getResultPrinciple.get(projectId = projectId, reportId) } returns resultPrinciple(projectResult(resultNumber))

        assertThat(controller.getResultAndPrinciple(projectId = projectId, reportId))
            .isEqualTo(resultPrincipleDto(resultNumber))
    }

    @Test
    fun update() {
        val projectId = 3L
        val reportId = 4L
        val resultNumber = 5
        val resultPrinciple = resultPrinciple(projectResult(resultNumber))
        val updateResultPrincipleDTO = updateResultPrincipleDto(resultNumber)

        every { updateResultPrinciple.update(projectId, reportId, any()) } returns resultPrinciple

        val result = controller.updateResultAndPrinciple(projectId, reportId, updateResultPrincipleDTO)

        compare(result, resultPrincipleDto(resultNumber))
        verify(exactly = 1) { updateResultPrinciple.update(projectId, reportId, any()) }
    }

    @Test
    fun upload() {
        val projectId = 4L
        val reportId = 5L
        val resultNumber = 6
        val jemsFileMetadata = JemsFileMetadata(7L, "name", ZonedDateTime.now())
        val multipartFile = mockk<MultipartFile> {
            every { inputStream } returns mockk()
            every { originalFilename } returns "multipart"
            every { size } returns 100
        }

        every { uploadAttachment.upload(projectId, reportId, resultNumber, any()) } returns jemsFileMetadata

        val result = controller.uploadAttachmentToResult(projectId, reportId, resultNumber, multipartFile)

        assertThat(result.id).isEqualTo(jemsFileMetadata.id)
        assertThat(result.name).isEqualTo(jemsFileMetadata.name)
        assertThat(result.uploaded).isEqualTo(jemsFileMetadata.uploaded)
        val slot = slot<ProjectFile>()
        verify(exactly = 1) { uploadAttachment.upload(projectId, reportId, resultNumber, capture(slot)) }
        assertThat(slot.captured.stream).isEqualTo(multipartFile.inputStream)
        assertThat(slot.captured.name).isEqualTo(multipartFile.originalFilename)
        assertThat(slot.captured.size).isEqualTo(multipartFile.size)
    }

    @Test
    fun download() {
        val projectId = 5L
        val reportId = 6L
        val resultNumber = 7
        val file = Pair("file-name", byteArrayOf(Byte.MIN_VALUE, Byte.MAX_VALUE))

        every { downloadAttachment.download(projectId, reportId, resultNumber) } returns file

        val result = controller.downloadAttachmentFromResult(projectId, reportId, resultNumber)
        assertThat(result.body?.byteArray).isEqualTo(file.second)
        assertThat(result.headers[HttpHeaders.CONTENT_DISPOSITION]).containsExactly("attachment; filename*=UTF-8''file-name")
    }

    @Test
    fun delete() {
        every { deleteAttachment.delete(any(), any(), any()) } returns Unit

        controller.deleteAttachmentFromResult(2L, 3L, 4)

        verify(exactly = 1) { deleteAttachment.delete(2L, 3L, 4) }
    }

    private fun compare(result: ProjectReportResultPrincipleDTO, expected: ProjectReportResultPrincipleDTO) {
        val projectResultActual = result.projectResults[0]
        val projectResultExpected = expected.projectResults[0]
        assertThat(result.projectResults.size).isEqualTo(expected.projectResults.size)
        assertThat(projectResultActual.resultNumber).isEqualTo(projectResultExpected.resultNumber)
        assertThat(projectResultActual.baseline).isEqualTo(projectResultExpected.baseline)
        assertThat(projectResultActual.targetValue).isEqualTo(projectResultExpected.targetValue)
        assertThat(projectResultActual.currentReport).isEqualTo(projectResultExpected.currentReport)
        assertThat(projectResultActual.previouslyReported).isEqualTo(projectResultExpected.previouslyReported)

        val effectActual = result.horizontalPrinciples
        val principlesExpected = expected.horizontalPrinciples
        assertThat(effectActual.sustainableDevelopmentCriteriaEffect).isEqualTo(principlesExpected.sustainableDevelopmentCriteriaEffect)
        assertThat(effectActual.equalOpportunitiesEffect).isEqualTo(principlesExpected.equalOpportunitiesEffect)
        assertThat(effectActual.sexualEqualityEffect).isEqualTo(principlesExpected.sexualEqualityEffect)
    }
}
