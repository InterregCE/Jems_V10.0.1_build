package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.ChecklistComponentInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.HeadlineInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.OptionsToggleInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.TextInputInstanceMetadataDTO
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.controller.verificationChecklist.VerificationChecklistInstanceController
import io.cloudflight.jems.server.project.service.checklist.clone.verification.CloneVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.verification.CreateVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.verification.DeleteVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.verification.ExportVerificationChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.verification.GetVerificationChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.verification.GetVerificationChecklistsInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.update.verification.UpdateVerificationChecklistInstanceInteractor
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class VerificationChecklistInstanceControllerTest : UnitTest() {

    companion object {
        const val projectId = 1L
        const val reportId = 20L
        const val checklistId = 5L
        private val TODAY = ZonedDateTime.now()
    }

    private val checklist = ChecklistInstance(
        id = checklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.VERIFICATION,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = reportId,
        programmeChecklistId = 1L,
        visible = false,
        description = "test"
    )

    private val checklistDto = ChecklistInstanceDTO(
        id = checklistId,
        status = ChecklistInstanceStatusDTO.DRAFT,
        type = ProgrammeChecklistTypeDTO.VERIFICATION,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = reportId,
        programmeChecklistId = 1L,
        visible = false,
        description = "test",
        finishedDate = null,
        createdAt = null,
    )

    private val checklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        relatedToId = reportId,
        finishedDate = null,
        type = ProgrammeChecklistType.VERIFICATION,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        creatorEmail = "a@a",
        creatorId = 200L,
        createdAt = TODAY,
        consolidated = false,
        visible = true,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                0,
                HeadlineMetadata("headline"),
                HeadlineInstanceMetadata()
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                1,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe"),
                OptionsToggleInstanceMetadata("yes", "test")
            ),
            ChecklistComponentInstance(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                1,
                TextInputMetadata("Question for the text input", "Explanation label", 1000),
                TextInputInstanceMetadata("The reason is")
            )
        )
    )

    private val checklistDetailDTO = ChecklistInstanceDetailDTO(
        id = checklistId,
        status = ChecklistInstanceStatusDTO.DRAFT,
        finishedDate = null,
        name = "test",
        creatorEmail = "a@a",
        createdAt = TODAY,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        relatedToId = 20L,
        components = mutableListOf(
            ChecklistComponentInstanceDTO(
                2L,
                ProgrammeChecklistComponentTypeDTO.HEADLINE,
                0,
                HeadlineMetadataDTO("headline"),
                HeadlineInstanceMetadataDTO()
            ),
            ChecklistComponentInstanceDTO(
                3L,
                ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE,
                1,
                OptionsToggleMetadataDTO("What option do you choose", "yes", "no", "maybe"),
                OptionsToggleInstanceMetadataDTO("yes", "test")
            ),
            ChecklistComponentInstanceDTO(
                4L,
                ProgrammeChecklistComponentTypeDTO.TEXT_INPUT,
                1,
                TextInputMetadataDTO("Question for the text input", "Explanation label", 1000),
                TextInputInstanceMetadataDTO("The reason is")
            )
        )
    )

    private val createChecklist = CreateChecklistInstanceModel(
        projectId,
        1L
    )

    private val createChecklistInstanceDTO = CreateChecklistInstanceDTO(
        projectId,
        1L
    )

    @InjectMockKs
    lateinit var controller: VerificationChecklistInstanceController

    @MockK
    lateinit var getVerificationChecklistInteractor: GetVerificationChecklistsInstancesInteractor

    @MockK
    lateinit var getVerificationChecklistDetailInteractor: GetVerificationChecklistInstanceDetailInteractor

    @MockK
    lateinit var updateInteractor: UpdateVerificationChecklistInstanceInteractor

    @MockK
    lateinit var createInteractor: CreateVerificationChecklistInstanceInteractor

    @MockK
    lateinit var deleteInteractor: DeleteVerificationChecklistInstanceInteractor

    @MockK
    lateinit var exportInteractor: ExportVerificationChecklistInstanceInteractor

    @MockK
    lateinit var cloneInteractor: CloneVerificationChecklistInstanceInteractor

    @Test
    fun `get verification checklists`() {
        every { getVerificationChecklistInteractor.getVerificationChecklistInstances(projectId, reportId) } returns listOf(checklist)
        Assertions.assertThat(controller.getAllVerificationChecklistInstances(projectId, reportId))
            .isEqualTo(listOf(checklistDto))
    }

    @Test
    fun `get verification checklist details`() {
        every { getVerificationChecklistDetailInteractor.getVerificationChecklistInstanceDetail(projectId, reportId, checklistId) } returns checklistDetail
        Assertions.assertThat(controller.getVerificationChecklistInstanceDetail(projectId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `delete verification checklist`() {
        every { deleteInteractor.deleteById(projectId, reportId, checklistId) } just Runs
        assertDoesNotThrow { controller.deleteVerificationChecklistInstance(projectId, reportId, checklistId) }
    }

    @Test
    fun `update verification checklist description`() {
        every { updateInteractor.updateDescription(projectId, reportId, checklistId, "test") } returns checklist
        Assertions.assertThat(controller.updateVerificationChecklistDescription(projectId, reportId, checklistId, "test"))
            .isEqualTo(checklistDto)
    }

    @Test
    fun `create verification checklist`() {
        every { createInteractor.create(projectId, reportId, createChecklist) } returns checklistDetail
        Assertions.assertThat(controller.createVerificationChecklistInstance(projectId, reportId, createChecklistInstanceDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `export verification checklist`() {
        val exportResult = ExportResult(
            "content-type",
            "filename.pdf",
            ByteArray(10),
        )
        every { exportInteractor.export(projectId, reportId, checklistId, SystemLanguage.CS, null) } returns exportResult
        Assertions.assertThat(controller.exportVerificationChecklistInstance(projectId, reportId, checklistId, SystemLanguage.CS, null))
            .isEqualTo(exportResult.toResponseEntity())
    }

    @Test
    fun `clone verification checklist`() {
        every { cloneInteractor.clone(projectId, reportId, checklistId) } returns checklistDetail
        Assertions.assertThat(controller.cloneVerificationChecklistInstance(projectId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }
}
