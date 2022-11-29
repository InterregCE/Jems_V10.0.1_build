package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.*
import io.cloudflight.jems.api.project.dto.checklist.metadata.HeadlineInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.OptionsToggleInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.TextInputInstanceMetadataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.controller.controlChecklist.ControlChecklistInstanceController
import io.cloudflight.jems.server.project.service.checklist.create.control.CreateControlChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.control.DeleteControlChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.control.GetControlChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.control.GetControlChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.update.control.UpdateControlChecklistInstanceInteractor
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal

internal class ControlChecklistInstanceControllerTest: UnitTest() {

    companion object {
        const val partnerId = 1L
        const val reportId = 20L
        const val checklistId = 5L
    }

    private val checklist = ChecklistInstance(
        id = checklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
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
        type = ProgrammeChecklistTypeDTO.CONTROL,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = reportId,
        programmeChecklistId = 1L,
        visible = false,
        description = "test",
        finishedDate = null
    )

    private val checklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        relatedToId = reportId,
        finishedDate = null,
        type = ProgrammeChecklistType.CONTROL,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        creatorEmail = "a@a",
        creatorId = 200L,
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
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
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
        partnerId,
        1L
    )

    private val createChecklistInstanceDTO = CreateChecklistInstanceDTO(
        partnerId,
        1L
    )

    @InjectMockKs
    lateinit var controller: ControlChecklistInstanceController

    @MockK
    lateinit var getControlChecklistInteractor: GetControlChecklistInstancesInteractor

    @MockK
    lateinit var getControlChecklistDetailInteractor: GetControlChecklistInstanceDetailInteractor

    @MockK
    lateinit var updateInteractor: UpdateControlChecklistInstanceInteractor

    @MockK
    lateinit var createInteractor: CreateControlChecklistInstanceInteractor

    @MockK
    lateinit var deleteInteractor: DeleteControlChecklistInstanceInteractor

    @Test
    fun `get control checklists`() {
        every { getControlChecklistInteractor.getControlChecklistInstances(partnerId, reportId) } returns listOf(checklist)
        Assertions.assertThat(controller.getAllControlChecklistInstances(partnerId, reportId))
            .isEqualTo(listOf(checklistDto))
    }

    @Test
    fun `get control checklist details`() {
        every { getControlChecklistDetailInteractor.getControlChecklistInstanceDetail(partnerId, reportId, checklistId) } returns checklistDetail
        Assertions.assertThat(controller.getControlChecklistInstanceDetail(partnerId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `delete control checklist`() {
        every { deleteInteractor.deleteById(partnerId, reportId,checklistId) } just Runs
        assertDoesNotThrow { controller.deleteControlChecklistInstance(partnerId, reportId, checklistId) }
    }

    @Test
    fun `update control checklist description`() {
        every { updateInteractor.updateDescription(partnerId, reportId, checklistId, "test") } returns checklist
        Assertions.assertThat(controller.updateControlChecklistDescription(partnerId, reportId, checklistId, "test"))
            .isEqualTo(checklistDto)
    }

    @Test
    fun `create control checklist`() {
        every { createInteractor.create(partnerId, reportId, createChecklist) } returns checklistDetail
        Assertions.assertThat(controller.createControlChecklistInstance(partnerId, reportId, createChecklistInstanceDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }
}
