package io.cloudflight.jems.server.project.controller.report.project.workPlan

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.report.project.workPlan.*
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.project.controller.report.partner.dummyFile
import io.cloudflight.jems.server.project.controller.report.partner.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.partner.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.partner.dummyMultipartFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.*
import io.cloudflight.jems.server.project.service.report.project.workPlan.getProjectReportWorkPlan.GetProjectReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.project.workPlan.updateProjectReportWorkPlan.UpdateProjectReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.project.workPlan.uploadFileToProjectReportWorkPlan.UploadFileToProjectReportWorkPlanInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class ProjectReportWorkPlanControllerTest : UnitTest() {

    private val time = ZonedDateTime.now()

    private val workPlan = listOf(
        ProjectReportWorkPackage(
            id = 18L,
            number = 4,
            deactivated = false,
            specificObjective = setOf(InputTranslation(SystemLanguage.EN, "spec-obj-EN")),
            specificStatus = ProjectReportWorkPlanStatus.Partly,
            specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "spec-expl-EN")),
            communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "comm-obj-EN")),
            communicationStatus = ProjectReportWorkPlanStatus.Fully,
            communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "comm-expl-EN")),
            completed = true,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc-EN")),
            activities = listOf(
                ProjectReportWorkPackageActivity(
                    id = 45L,
                    number = 7,
                    title = setOf(InputTranslation(SystemLanguage.EN, "act-title-EN")),
                    deactivated = false,
                    startPeriod = ProjectPeriod(1, 1, 3),
                    endPeriod = null,
                    status = ProjectReportWorkPlanStatus.Not,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "act-progress-EN")),
                    attachment = JemsFileMetadata(977L, "file", time),
                    deliverables = listOf(
                        ProjectReportWorkPackageActivityDeliverable(
                            id = 665L,
                            number = 44,
                            title = setOf(InputTranslation(SystemLanguage.EN, "act-del-title-EN")),
                            deactivated = true,
                            period = ProjectPeriod(2, 4, 6),
                            previouslyReported = BigDecimal.valueOf(422L, 2),
                            currentReport = BigDecimal.valueOf(512L, 2),
                            progress = setOf(InputTranslation(SystemLanguage.EN, "act-del-progress-EN")),
                            attachment = JemsFileMetadata(6336L, "file2", time),
                            previousCurrentReport = BigDecimal.valueOf(512L, 2),
                            previousProgress = setOf(InputTranslation(SystemLanguage.EN, "act-del-progress-EN")),
                        ),
                    ),
                    previousStatus = ProjectReportWorkPlanStatus.Not,
                    previousProgress = setOf(InputTranslation(SystemLanguage.EN, "act-progress-EN")),
                ),
            ),
            outputs = listOf(
                ProjectReportWorkPackageOutput(
                    id = 115L,
                    number = 3,
                    title = setOf(InputTranslation(SystemLanguage.EN, "out-title-EN")),
                    deactivated = false,
                    outputIndicator = OutputIndicatorSummary(77L, "ident", "code",
                        name = setOf(InputTranslation(SystemLanguage.EN, "indicatorName")), "prio-code", setOf(InputTranslation(SystemLanguage.EN, "measure"))),
                    period = ProjectPeriod(3, 7, 9),
                    targetValue = BigDecimal.valueOf(355L, 2),
                    currentReport = BigDecimal.valueOf(455L, 2),
                    previouslyReported = BigDecimal.valueOf(555L, 2),
                    progress = setOf(InputTranslation(SystemLanguage.EN, "out-progress-EN")),
                    attachment = JemsFileMetadata(6377L, "file3", time),
                    previousProgress = setOf(InputTranslation(SystemLanguage.EN, "out-progress-EN")),
                    previousCurrentReport = BigDecimal.valueOf(455L, 2)
                ),
            ),
            investments = emptyList(),
            previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "spec-expl-EN")),
            previousCommunicationStatus = ProjectReportWorkPlanStatus.Fully,
            previousCompleted = true,
            previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "comm-expl-EN")),
            previousSpecificStatus = ProjectReportWorkPlanStatus.Partly,
            previousDescription = setOf(InputTranslation(SystemLanguage.EN, "desc-EN")),
            )
    )
    private val workPlanExpected = ProjectReportWorkPackageDTO(
        id = 18L,
        number = 4,
        deactivated = false,
        specificObjective = setOf(InputTranslation(SystemLanguage.EN, "spec-obj-EN")),
        specificStatus = ProjectReportWorkPlanStatusDTO.Partly,
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "spec-expl-EN")),
        communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "comm-obj-EN")),
        communicationStatus = ProjectReportWorkPlanStatusDTO.Fully,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "comm-expl-EN")),
        completed = true,
        description = setOf(InputTranslation(SystemLanguage.EN, "desc-EN")),
        activities = listOf(
            ProjectReportWorkPackageActivityDTO(
                id = 45L,
                number = 7,
                title = setOf(InputTranslation(SystemLanguage.EN, "act-title-EN")),
                deactivated = false,
                startPeriod = ProjectPeriodDTO(0, 1, 1, 3),
                endPeriod = null,
                status = ProjectReportWorkPlanStatusDTO.Not,
                progress = setOf(InputTranslation(SystemLanguage.EN, "act-progress-EN")),
                attachment = JemsFileMetadataDTO(977L, "file", time),
                deliverables = listOf(
                    ProjectReportWorkPackageActivityDeliverableDTO(
                        id = 665L,
                        number = 44,
                        title = setOf(InputTranslation(SystemLanguage.EN, "act-del-title-EN")),
                        deactivated = true,
                        period = ProjectPeriodDTO(0, 2, 4, 6),
                        previouslyReported = BigDecimal.valueOf(422L, 2),
                        currentReport = BigDecimal.valueOf(512L, 2),
                        progress = setOf(InputTranslation(SystemLanguage.EN, "act-del-progress-EN")),
                        attachment = JemsFileMetadataDTO(6336L, "file2", time),
                        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "act-del-progress-EN")),
                        previousCurrentReport = BigDecimal.valueOf(512L, 2)
                    ),
                ),
                previousProgress = setOf(InputTranslation(SystemLanguage.EN, "act-progress-EN")),
                previousStatus = ProjectReportWorkPlanStatusDTO.Not
            ),
        ),
        outputs = listOf(
            ProjectReportWorkPackageOutputDTO(
                id = 115L,
                number = 3,
                title = setOf(InputTranslation(SystemLanguage.EN, "out-title-EN")),
                deactivated = false,
                outputIndicator = OutputIndicatorSummaryDTO(77L, "ident", "code",
                    name = setOf(InputTranslation(SystemLanguage.EN, "indicatorName")), "prio-code", setOf(InputTranslation(SystemLanguage.EN, "measure"))),
                period = ProjectPeriodDTO(0L, 3, 7, 9),
                targetValue = BigDecimal.valueOf(355L, 2),
                currentReport = BigDecimal.valueOf(455L, 2),
                previouslyReported = BigDecimal.valueOf(555L, 2),
                progress = setOf(InputTranslation(SystemLanguage.EN, "out-progress-EN")),
                attachment = JemsFileMetadataDTO(6377L, "file3", time),
                previousProgress = setOf(InputTranslation(SystemLanguage.EN, "out-progress-EN")),
                previousCurrentReport = BigDecimal.valueOf(455L, 2),
            ),
        ),
        investments = emptyList(),
        previousSpecificStatus = ProjectReportWorkPlanStatusDTO.Partly,
        previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "comm-expl-EN")),
        previousCompleted = true,
        previousCommunicationStatus = ProjectReportWorkPlanStatusDTO.Fully,
        previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "spec-expl-EN")),
        previousDescription = setOf(InputTranslation(SystemLanguage.EN, "desc-EN")),
    )

    private val toUpdate = UpdateProjectReportWorkPackageDTO(
        id = 75L,
        specificStatus = ProjectReportWorkPlanStatusDTO.Partly,
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "spec-expl-EN-update")),
        communicationStatus = ProjectReportWorkPlanStatusDTO.Not,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "comm-expl-EN-update")),
        completed = false,
        description = setOf(InputTranslation(SystemLanguage.EN, "desc-EN-update")),
        activities = listOf(
            UpdateProjectReportWorkPackageActivityDTO(
                id = 421L,
                status = ProjectReportWorkPlanStatusDTO.Partly,
                progress = setOf(InputTranslation(SystemLanguage.EN, "act-progress-EN-update")),
                deliverables = listOf(
                    UpdateProjectReportWorkPackageActivityDeliverableDTO(
                        id = 661L,
                        currentReport = BigDecimal.valueOf(8956L, 2),
                        progress = setOf(InputTranslation(SystemLanguage.EN, "act-del-progress-EN-update")),
                    ),
                )
            ),
        ),
        outputs = listOf(
            UpdateProjectReportWorkPackageOutputDTO(
                id = 110L,
                currentReport = BigDecimal.valueOf(112L, 2),
                progress = setOf(InputTranslation(SystemLanguage.EN, "out-progress-EN-update")),
            ),
        ),
        investments = emptyList()

    )

    private val toUpdateExpected = ProjectReportWorkPackageUpdate(
        id = 75L,
        specificStatus = ProjectReportWorkPlanStatus.Partly,
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "spec-expl-EN-update")),
        communicationStatus = ProjectReportWorkPlanStatus.Not,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "comm-expl-EN-update")),
        completed = false,
        description = setOf(InputTranslation(SystemLanguage.EN, "desc-EN-update")),
        activities = listOf(
            ProjectReportWorkPackageActivityUpdate(
                id = 421L,
                status = ProjectReportWorkPlanStatus.Partly,
                progress = setOf(InputTranslation(SystemLanguage.EN, "act-progress-EN-update")),
                deliverables = listOf(
                    ProjectReportWorkPackageActivityDeliverableUpdate(
                        id = 661L,
                        currentReport = BigDecimal.valueOf(8956L, 2),
                        progress = setOf(InputTranslation(SystemLanguage.EN, "act-del-progress-EN-update")),
                    ),
                )
            ),
        ),
        outputs = listOf(
            ProjectReportWorkPackageOutputUpdate(
                id = 110L,
                currentReport = BigDecimal.valueOf(112L, 2),
                progress = setOf(InputTranslation(SystemLanguage.EN, "out-progress-EN-update")),
            ),
        ),
        investments = emptyList()
    )

    @MockK
    private lateinit var getReportWorkPlan: GetProjectReportWorkPlanInteractor
    @MockK
    private lateinit var updateReportWorkPlan: UpdateProjectReportWorkPlanInteractor
    @MockK
    private lateinit var uploadFileToReportWorkPlan: UploadFileToProjectReportWorkPlanInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportWorkPlanController

    @BeforeEach
    fun resetMocks() {
        clearMocks(getReportWorkPlan, updateReportWorkPlan, uploadFileToReportWorkPlan)
    }

    @Test
    fun getWorkPlan() {
        every { getReportWorkPlan.get(15L, reportId = 17L) } returns workPlan
        assertThat(controller.getWorkPlan(15L, reportId = 17L)).containsExactly(workPlanExpected)
    }

    @Test
    fun updateWorkPlan() {
        val toUpdateSlot = slot<List<ProjectReportWorkPackageUpdate>>()
        every { updateReportWorkPlan.update(16L, reportId = 18L, capture(toUpdateSlot)) } returns workPlan

        assertThat(controller.updateWorkPlan(16L, reportId = 18L, listOf(toUpdate))).containsExactly(workPlanExpected)
        assertThat(toUpdateSlot.captured.first()).isEqualTo(toUpdateExpected)
    }

    @Test
    fun uploadFileToActivity() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToReportWorkPlan.uploadToActivity(17L, 20L, 25L, 30L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToActivity(17L, 20L, 25L, 30L, dummyMultipartFile(originalName = "file_name.ext")))
            .isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadFileToDeliverable() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToReportWorkPlan.uploadToDeliverable(18L, 21L, 26L, 31L, 36L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToDeliverable(18L, 21L, 26L, 31L, 36L, dummyMultipartFile(originalName = "file_name.ext")))
            .isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadFileToOutput() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToReportWorkPlan.uploadToOutput(19L, 22L, 27L, 32L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToOutput(19L, 22L, 27L, 32L, dummyMultipartFile(originalName = "file_name.ext")))
            .isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

}
