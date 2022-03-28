package io.cloudflight.jems.server.project.controller.report.workPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageOutputDTO
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan.GetProjectPartnerReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan.UpdateProjectPartnerReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport.UploadFileToProjectPartnerReportWorkPlanInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.multipart.MultipartFile
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
class ProjectPartnerReportWorkPlanControllerTest {

    companion object {
        private const val PARTNER_ID = 447L
        private const val REPORT_ID = 466L

        private val UPLOADED = ZonedDateTime.now()

        private val dummyWorkPlan = ProjectPartnerReportWorkPackage(
            id = 754,
            number = 1,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivity(
                    id = 755,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "A1.1")),
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 756,
                            number = 1,
                            title = setOf(InputTranslation(SystemLanguage.EN, "D1.1.1")),
                            contribution = true,
                            evidence = false,
                            attachment = ProjectReportFileMetadata(
                                id = 980L,
                                name = "cat.gif",
                                uploaded = UPLOADED,
                            )
                        )
                    ),
                    attachment = null,
                ),
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutput(
                    id = 757,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "O1")),
                    contribution = true,
                    evidence = false,
                    attachment = null,
                )
            ),
        )

        private val dummyWorkPlanDto = ProjectPartnerReportWorkPackageDTO(
            id = 754,
            number = 1,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivityDTO(
                    id = 755,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "A1.1")),
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverableDTO(
                            id = 756,
                            number = 1,
                            title = setOf(InputTranslation(SystemLanguage.EN, "D1.1.1")),
                            contribution = true,
                            evidence = false,
                            attachment = ProjectReportFileMetadataDTO(
                                id = 980L,
                                name = "cat.gif",
                                uploaded = UPLOADED,
                            )
                        )
                    ),
                    attachment = null,
                ),
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutputDTO(
                    id = 757,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "O1")),
                    contribution = true,
                    evidence = false,
                    attachment = null,
                )
            ),
        )

        private val dummyWorkPlanUpdateDto = UpdateProjectPartnerReportWorkPackageDTO(
            id = 754,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                UpdateProjectPartnerReportWorkPackageActivityDTO(
                    id = 755,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO(
                            id = 756,
                            contribution = true,
                            evidence = false,
                        )
                    ),
                ),
            ),
            outputs = listOf(
                UpdateProjectPartnerReportWorkPackageOutputDTO(
                    id = 757,
                    contribution = true,
                    evidence = false,
                )
            ),
        )

        private val expectedDummyWorkPlanUpdateModel = UpdateProjectPartnerReportWorkPackage(
            id = 754,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                UpdateProjectPartnerReportWorkPackageActivity(
                    id = 755,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        UpdateProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 756,
                            contribution = true,
                            evidence = false,
                        )
                    ),
                ),
            ),
            outputs = listOf(
                UpdateProjectPartnerReportWorkPackageOutput(
                    id = 757,
                    contribution = true,
                    evidence = false,
                )
            ),
        )

        private val stream = ByteArray(5).inputStream()

        private val dummyFile = ProjectReportFileMetadata(id = 80L, "file_name.ext", uploaded = UPLOADED)
        private val dummyFileDto = ProjectReportFileMetadataDTO(id = 80L, "file_name.ext", uploaded = UPLOADED)
        private val dummyFileExpected = ProjectFile(stream, "file_name.ext", 50L)
        private fun dummyMultipartFile(name: String = "file_name.ext", originalName: String? = null): MultipartFile {
            val file = mockk<MultipartFile>()
            every { file.inputStream } returns stream
            every { file.originalFilename } returns originalName
            every { file.name } returns name
            every { file.size } returns 50L
            return file
        }

    }

    @MockK
    lateinit var getPartnerReportWorkPlan: GetProjectPartnerReportWorkPlanInteractor

    @MockK
    lateinit var updatePartnerReportWorkPlan: UpdateProjectPartnerReportWorkPlanInteractor

    @MockK
    lateinit var uploadFileToPartnerReportWorkPlan: UploadFileToProjectPartnerReportWorkPlanInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportWorkPlanController

    @Test
    fun getWorkPlan() {
        every { getPartnerReportWorkPlan.getForPartner(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            listOf(dummyWorkPlan)
        assertThat(controller.getWorkPlan(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .containsExactly(dummyWorkPlanDto)
    }

    @Test
    fun update() {
        val slotData = slot<List<UpdateProjectPartnerReportWorkPackage>>()
        every { updatePartnerReportWorkPlan.update(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            workPlan = capture(slotData),
        ) } returns listOf(dummyWorkPlan)

        assertThat(controller.updateWorkPlan(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            workPackages = listOf(dummyWorkPlanUpdateDto),
        )).containsExactly(dummyWorkPlanDto)

        assertThat(slotData.captured).hasSize(1)
        assertThat(slotData.captured.first()).isEqualTo(expectedDummyWorkPlanUpdateModel)
    }

    @Test
    fun uploadFileToActivity() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToPartnerReportWorkPlan.uploadToActivity(PARTNER_ID, REPORT_ID, 45L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToActivity(PARTNER_ID, REPORT_ID, 45L, dummyMultipartFile(originalName = "file_name.ext"))).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadFileToDeliverable() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToPartnerReportWorkPlan.uploadToDeliverable(PARTNER_ID, REPORT_ID, 30L, 32L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToDeliverable(PARTNER_ID, REPORT_ID, 30L, 32L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadFileToOutput() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToPartnerReportWorkPlan.uploadToOutput(PARTNER_ID, REPORT_ID, 75L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToOutput(PARTNER_ID, REPORT_ID, 75L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

}
