package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.*
import io.cloudflight.jems.api.project.dto.checklist.metadata.HeadlineInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.OptionsToggleInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.TextInputInstanceMetadataDTO
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.controller.closureChecklist.ClosureChecklistInstanceController
import io.cloudflight.jems.server.project.service.checklist.clone.closure.CloneClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.closure.CreateClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.closure.DeleteClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.closure.ExportClosureChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.closure.GetClosureChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.closure.GetClosureChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.update.closure.UpdateClosureChecklistInstanceInteractor
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import java.time.ZonedDateTime

class ClosureChecklistInstanceControllerTest: UnitTest() {

    companion object {
        const val projectId = 1L
        const val reportId = 20L
        const val checklistId = 5L
        const val programmeChecklistId = 100L
        private val TODAY = ZonedDateTime.now()
    }

    private val checklist = ChecklistInstance(
        id = checklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CLOSURE,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = reportId,
        programmeChecklistId = programmeChecklistId,
        visible = false,
        description = "test"
    )

    private val checklistDto = ChecklistInstanceDTO(
        id = checklistId,
        status = ChecklistInstanceStatusDTO.DRAFT,
        type = ProgrammeChecklistTypeDTO.CLOSURE,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = reportId,
        programmeChecklistId = programmeChecklistId,
        visible = false,
        description = "test",
        finishedDate = null,
        createdAt = null,
    )

    private val checklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        relatedToId = reportId,
        finishedDate = null,
        type = ProgrammeChecklistType.CLOSURE,
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

    @InjectMockKs
    lateinit var controller: ClosureChecklistInstanceController

    @MockK
    lateinit var getClosureChecklistInteractor: GetClosureChecklistInstancesInteractor

    @MockK
    lateinit var getClosureChecklistDetailInteractor: GetClosureChecklistInstanceDetailInteractor

    @MockK
    lateinit var updateInteractor: UpdateClosureChecklistInstanceInteractor

    @MockK
    lateinit var createInteractor: CreateClosureChecklistInstanceInteractor

    @MockK
    lateinit var deleteInteractor: DeleteClosureChecklistInstanceInteractor

    @MockK
    lateinit var exportInteractor: ExportClosureChecklistInstanceInteractor

    @MockK
    lateinit var cloneInteractor: CloneClosureChecklistInstanceInteractor

    @Test
    fun `get closure checklists`() {
        every { getClosureChecklistInteractor.getClosureChecklistInstances(projectId, reportId) } returns listOf(checklist)
        assertThat(controller.getAllClosureChecklistInstances(projectId, reportId)).isEqualTo(listOf(checklistDto))
    }

    @Test
    fun `get closure checklist details`() {
        every { getClosureChecklistDetailInteractor.getClosureChecklistInstanceDetail(
            projectId, reportId, checklistId
        ) } returns checklistDetail
        assertThat(controller.getClosureChecklistInstanceDetail(projectId, reportId, checklistId))
            .usingRecursiveComparison().isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `delete closure checklist`() {
        every { deleteInteractor.deleteById(reportId, checklistId) } just Runs
        assertDoesNotThrow { controller.deleteClosureChecklistInstance(projectId, reportId, checklistId) }
    }

    @Test
    fun `update closure checklist description`() {
        every { updateInteractor.updateDescription(reportId, checklistId, "test") } returns checklist
        assertThat(controller.updateClosureChecklistDescription(projectId, reportId, checklistId, "test"))
            .isEqualTo(checklistDto)
    }

    @Test
    fun `create closure checklist`() {
        every { createInteractor.create(reportId, programmeChecklistId) } returns checklistDetail
        assertThat(controller.createClosureChecklistInstance(projectId, reportId, programmeChecklistId))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `export closure checklist`() {
        val exportResult = ExportResult(
            "content-type",
            "filename.pdf",
            ByteArray(10),
        )
        every { exportInteractor.export(projectId, reportId, checklistId, SystemLanguage.CS, null) } returns exportResult
        assertThat(controller.exportClosureChecklistInstance(projectId, reportId, checklistId, SystemLanguage.CS, null))
            .isEqualTo(exportResult.toResponseEntity())
    }

    @Test
    fun `clone closure checklist`() {
        every { cloneInteractor.clone(reportId, checklistId) } returns checklistDetail
        assertThat(controller.cloneClosureChecklistInstance(projectId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }
}
