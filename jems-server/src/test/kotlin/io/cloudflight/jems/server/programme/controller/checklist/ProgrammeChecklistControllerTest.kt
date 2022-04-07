package io.cloudflight.jems.server.programme.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDetailDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.create.CreateProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.getDetail.GetProgrammeChecklistDetailInteractor
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateProgrammeChecklistInteractor
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.ZoneId
import java.time.ZonedDateTime

class ProgrammeChecklistControllerTest : UnitTest() {

    private val ID = 1L
    private val checklist = ProgrammeChecklist(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
    )
    private val checklistDTO = ProgrammeChecklistDTO(
        id = ID,
        type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
    )

    private val components = mutableListOf(
        ProgrammeChecklistComponent(
            ID,
            ProgrammeChecklistComponentType.HEADLINE,
            1,
            HeadlineMetadata(
                value = "this is headline.json"
            )
        ),
        ProgrammeChecklistComponent(
            2L,
            ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
            2,
            OptionsToggleMetadata(
                question = "question 1",
                firstOption = "yes",
                secondOption = "no"
            )
        )
    )

    private val checklistDetail = ProgrammeChecklistDetail(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        components = components
    )

    private val componentsDTO = mutableListOf(
        ProgrammeChecklistComponentDTO(
            ID,
            ProgrammeChecklistComponentTypeDTO.HEADLINE,
            1,
            HeadlineMetadataDTO("this is headline.json")
        ),
        ProgrammeChecklistComponentDTO(
            2L,
            ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE,
            2,
            OptionsToggleMetadataDTO(
                "question 1",
                "yes",
                "no"
            )
        )
    )

    private val checklistDetailDTO = ProgrammeChecklistDetailDTO(
        id = ID,
        type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        components = componentsDTO
    )

    @MockK
    lateinit var getChecklistInteractor: GetProgrammeChecklistInteractor

    @MockK
    lateinit var updateInteractor: UpdateProgrammeChecklistInteractor

    @MockK
    lateinit var createInteractor: CreateProgrammeChecklistInteractor

    @MockK
    lateinit var getChecklistDetailInteractor: GetProgrammeChecklistDetailInteractor

    @MockK
    lateinit var deleteInteractor: DeleteProgrammeChecklistInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeChecklistController

    @Test
    fun `get checklists`() {
        every { getChecklistInteractor.getProgrammeChecklist() } returns listOf(checklist)
        assertThat(controller.getProgrammeChecklists().get(0))
            .usingRecursiveComparison()
            .isEqualTo(checklistDTO)
    }

    @Test
    fun `get checklist detail`() {
        every { getChecklistDetailInteractor.getProgrammeChecklistDetail(ID) } returns checklistDetail
        assertThat(controller.getProgrammeChecklistDetail(ID))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `create checklist`() {
        val checklistSlot = slot<ProgrammeChecklistDetail>()
        every { createInteractor.create(capture(checklistSlot)) } returnsArgument 0
        assertThat(controller.createProgrammeChecklist(checklistDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `update checklist detail`() {
        val checklistSlot = slot<ProgrammeChecklistDetail>()
        every { updateInteractor.update(capture(checklistSlot)) } returnsArgument 0
        assertThat(controller.updateProgrammeChecklist(checklistDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `delete checklist`() {
        every { deleteInteractor.deleteProgrammeChecklist(ID) } just Runs
        assertDoesNotThrow { deleteInteractor.deleteProgrammeChecklist(ID) }
    }

}
