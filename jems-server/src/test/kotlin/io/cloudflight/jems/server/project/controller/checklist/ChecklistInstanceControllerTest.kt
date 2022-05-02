package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistComponentInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.HeadlineInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.OptionsToggleInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.TextInputInstanceMetadataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.create.CreateChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.getDetail.GetChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.programme.service.checklist.getList.GetChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ChecklistInstanceControllerTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val createChecklist = CreateChecklistInstanceModel(
        RELATED_TO_ID,
        PROGRAMME_CHECKLIST_ID
    )

    private val createChecklistDTO = CreateChecklistInstanceDTO(
        RELATED_TO_ID,
        PROGRAMME_CHECKLIST_ID
    )

    private val checkLisDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "test",
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
                OptionsToggleInstanceMetadata("yes")
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

    private val checkLisDetailDTO = ChecklistInstanceDetailDTO(
        id = CHECKLIST_ID,
        status = ChecklistInstanceStatusDTO.DRAFT,
        finishedDate = null,
        name = "test",
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
                OptionsToggleInstanceMetadataDTO("yes")
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
        programmeChecklistId = PROGRAMME_CHECKLIST_ID
    )

    private val checklistDTO = ChecklistInstanceDTO(
        id = CHECKLIST_ID,
        status = ChecklistInstanceStatusDTO.DRAFT,
        finishedDate = null,
        type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID
    )


    @MockK
    lateinit var getChecklistInteractor: GetChecklistInstanceInteractor

    @MockK
    lateinit var updateInteractor: UpdateChecklistInstanceInteractor

    @MockK
    lateinit var createInteractor: CreateChecklistInstanceInteractor

    @MockK
    lateinit var getChecklistDetailInteractor: GetChecklistInstanceDetailInteractor

    @MockK
    lateinit var deleteInteractor: DeleteChecklistInstanceInteractor

    @InjectMockKs
    private lateinit var controller: ChecklistInstanceController

    @Test
    fun `get checklists`() {
        every {
            getChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        } returns listOf(checklist)
        assertThat(
            controller.getChecklistInstances(
                RELATED_TO_ID,
                ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT
            ).get(0)
        )
            .usingRecursiveComparison()
            .isEqualTo(checklistDTO)
    }

    @Test
    fun `get checklist detail`() {
        every { getChecklistDetailInteractor.getChecklistInstanceDetail(CHECKLIST_ID) } returns checkLisDetail
        assertThat(controller.getChecklistInstanceDetail(CHECKLIST_ID))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetailDTO)
    }

    @Test
    fun `create checklist`() {
        every { createInteractor.create(createChecklist) } returns checkLisDetail
        assertThat(controller.createChecklistInstance(createChecklistDTO))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetailDTO)
    }

    @Test
    fun `update checklist detail`() {
        every { updateInteractor.update(any()) } returns checkLisDetail
        assertThat(controller.updateChecklistInstance(checkLisDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetailDTO)
    }

    @Test
    fun `delete checklist`() {
        every { deleteInteractor.deleteById(CHECKLIST_ID) } just Runs
        assertDoesNotThrow { deleteInteractor.deleteById(CHECKLIST_ID) }
    }
}
