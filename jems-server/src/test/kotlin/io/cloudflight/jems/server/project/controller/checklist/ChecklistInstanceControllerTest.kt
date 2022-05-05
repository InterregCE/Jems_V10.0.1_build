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
import io.cloudflight.jems.server.project.service.checklist.create.CreateChecklistInstanceInteractor
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.GetChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getMyInstances.GetMyChecklistInstancesInteractor
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.update.UpdateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getAllInstances.GetAllChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal

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
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        creatorEmail = "a@a",
        consolidated = false,
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

    private val checkLisDetailDTO = ChecklistInstanceDetailDTO(
        id = CHECKLIST_ID,
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
    lateinit var getMyChecklistInteractor: GetMyChecklistInstancesInteractor

    @RelaxedMockK
    lateinit var getAllChecklistInteractor: GetAllChecklistInstancesInteractor

    @RelaxedMockK
    lateinit var updateInteractor: UpdateChecklistInstanceInteractor

    @MockK
    lateinit var createInteractor: CreateChecklistInstanceInteractor

    @MockK
    lateinit var getChecklistDetailInteractor: GetChecklistInstanceDetailInteractor

    @MockK
    lateinit var deleteInteractor: DeleteChecklistInstanceInteractor

    @RelaxedMockK
    lateinit var consolidateInteractor: ConsolidateChecklistInstanceInteractor

    @InjectMockKs
    private lateinit var controller: ChecklistInstanceController

    @Test
    fun `get my checklists`() {
        every {
            getMyChecklistInteractor.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
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
        controller.getAllChecklistInstances(
            RELATED_TO_ID,
            ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT
        )

        verify {
            getAllChecklistInteractor.getChecklistInstancesByTypeAndRelatedId(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        }
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
}
