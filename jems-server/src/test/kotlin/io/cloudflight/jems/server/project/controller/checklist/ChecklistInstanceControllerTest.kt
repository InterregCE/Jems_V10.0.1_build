package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.ChecklistComponentInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistConsolidatorOptionsDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceSelectionDTO
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
import io.cloudflight.jems.server.project.service.checklist.clone.CloneChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.create.CreateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.DeleteChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.export.ExportChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.GetChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.update.UpdateChecklistInstanceInteractor
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ChecklistInstanceControllerTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L
    private val CREATOR_ID = 1L
    private val TODAY = ZonedDateTime.now()

    private val createChecklist = CreateChecklistInstanceModel(
        RELATED_TO_ID,
        PROGRAMME_CHECKLIST_ID
    )

    private val createChecklistDTO = CreateChecklistInstanceDTO(
        RELATED_TO_ID,
        PROGRAMME_CHECKLIST_ID
    )

    private val checklistDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
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
        id = CHECKLIST_ID,
        status = ChecklistInstanceStatusDTO.DRAFT,
        finishedDate = null,
        name = "test",
        creatorEmail = "a@a",
        createdAt = TODAY,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        relatedToId = 2L,
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

    private val checklist = ChecklistInstance(
        id = CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        visible = false,
        description = "test"
    )
    private val checklistSelected = ChecklistInstance(
        id = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        finishedDate = LocalDate.now(),
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        visible = true,
        description = "test"
    )

    private val checklistDTO = ChecklistInstanceDTO(
        id = CHECKLIST_ID,
        status = ChecklistInstanceStatusDTO.DRAFT,
        finishedDate = null,
        type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        description = "test",
        createdAt = null,
    )

    @RelaxedMockK
    lateinit var getChecklistInteractor: GetChecklistInstancesInteractor

    @RelaxedMockK
    lateinit var updateInteractor: UpdateChecklistInstanceInteractor

    @MockK
    lateinit var createInteractor: CreateChecklistInstanceInteractor

    @MockK
    lateinit var getChecklistDetailInteractor: GetChecklistInstanceDetailInteractor

    @MockK
    lateinit var deleteInteractor: DeleteChecklistInstanceInteractor

    @MockK
    lateinit var exportInteractor: ExportChecklistInstanceInteractor

    @RelaxedMockK
    lateinit var consolidateInteractor: ConsolidateChecklistInstanceInteractor

    @MockK
    lateinit var cloneInteractor: CloneChecklistInstanceInteractor

    @InjectMockKs
    private lateinit var controller: ChecklistInstanceController

    @BeforeEach
    fun reset() {
        clearMocks(getChecklistInteractor)
    }

    @Test
    fun `get my checklists`() {
        every {
            getChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        } returns listOf(checklist)
        assertThat(
            controller.getMyChecklistInstances(
                RELATED_TO_ID,
                ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT
            ).get(0)
        )
            .usingRecursiveComparison()
            .isEqualTo(checklistDTO)
    }

    @Test
    fun `get all checklists`() {
        every {
            getChecklistInteractor.getChecklistInstancesByTypeAndRelatedId(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        } returns listOf(checklist)

        controller.getAllChecklistInstances(
            RELATED_TO_ID,
            ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT
        )

        verify {
            getChecklistInteractor.getChecklistInstancesByTypeAndRelatedId(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        }
    }

    @Test
    fun `get checklists for selection`() {
        every {
            getChecklistInteractor.getChecklistInstancesForSelection(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        } returns listOf(checklist, checklistSelected)

        val checklists = controller.getChecklistInstancesForSelection(
            RELATED_TO_ID,
            ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT
        )

        assertThat(checklists).contains(
            ChecklistInstanceSelectionDTO(
                id = CHECKLIST_ID,
                status = ChecklistInstanceStatusDTO.DRAFT,
                finishedDate = null,
                type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
                name = "name",
                relatedToId = RELATED_TO_ID,
                programmeChecklistId = PROGRAMME_CHECKLIST_ID,
                consolidated = false,
                visible = false,
                description = "test"
            ),
            ChecklistInstanceSelectionDTO(
                id = 1L,
                status = ChecklistInstanceStatusDTO.FINISHED,
                finishedDate = checklistSelected.finishedDate,
                type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
                name = "name",
                relatedToId = RELATED_TO_ID,
                programmeChecklistId = PROGRAMME_CHECKLIST_ID,
                consolidated = false,
                visible = true,
                description = "test"
            )
        )
    }

    @Test
    fun `get checklist detail`() {
        every { getChecklistDetailInteractor.getChecklistInstanceDetail(CHECKLIST_ID, RELATED_TO_ID) } returns checklistDetail
        assertThat(controller.getChecklistInstanceDetail(CHECKLIST_ID, RELATED_TO_ID))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `create checklist`() {
        every { createInteractor.create(createChecklist) } returns checklistDetail
        assertThat(controller.createChecklistInstance(createChecklistDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `update checklist detail`() {
        every { updateInteractor.update(any()) } returns checklistDetail
        assertThat(controller.updateChecklistInstance(checklistDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `clone checklist`() {
        every { cloneInteractor.clone(any()) } returns checklistDetail
        assertThat(controller.cloneChecklistInstance(CHECKLIST_ID))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `delete checklist`() {
        every { deleteInteractor.deleteById(CHECKLIST_ID, RELATED_TO_ID) } just Runs
        assertDoesNotThrow { controller.deleteChecklistInstance(CHECKLIST_ID, RELATED_TO_ID) }
    }

    @Test
    fun `change checklist status`() {
        controller.changeChecklistStatus(CHECKLIST_ID, ChecklistInstanceStatusDTO.FINISHED)

        verify { updateInteractor.changeStatus(CHECKLIST_ID, ChecklistInstanceStatus.FINISHED) }
    }

    @Test
    fun `consolidate checklist`() {
        controller.consolidateChecklistInstance(CHECKLIST_ID, ChecklistConsolidatorOptionsDTO(true))

        verify { consolidateInteractor.consolidateChecklistInstance(CHECKLIST_ID, true) }
    }

    @Test
    fun `update checklist selection`() {
        controller.updateChecklistInstanceSelection(mapOf(CHECKLIST_ID to true))
        verify { updateInteractor.updateSelection(mapOf(CHECKLIST_ID to true)) }
    }

    @Test
    fun `update checklist description`() {
        every { updateInteractor.updateDescription(1L, "test") } returns checklist
        assertThat(controller.updateChecklistDescription(1L, "test"))
            .isEqualTo(checklistDTO)
    }

    @Test
    fun `export checklist`() {
        val exportResult = ExportResult(
            "content-type",
            "filename.pdf",
            ByteArray(10),
        )

        every { exportInteractor.export(RELATED_TO_ID, CHECKLIST_ID, SystemLanguage.DA, null) } returns exportResult
        assertThat(controller.exportChecklistInstance(RELATED_TO_ID, CHECKLIST_ID, SystemLanguage.DA, null))
            .isEqualTo(exportResult.toResponseEntity())
    }
}
