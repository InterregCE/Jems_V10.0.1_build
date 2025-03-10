package io.cloudflight.jems.server.programme.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDetailDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.ScoreMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.clone.CloneProgrammeChecklistInteractor
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
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ScoreMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
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
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

class ProgrammeChecklistControllerTest : UnitTest() {

    private val ID = 1L
    private val cID = 2L // Separate ID for a "Contracting" checklist
    private val checklist = ProgrammeChecklist(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false
    )
    private val contractingChecklist = ProgrammeChecklist(
        id = cID,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false
    )
    private val checklistDTO = ProgrammeChecklistDTO(
        id = ID,
        type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        name = "test",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
    )
    private val contractingChecklistDTO = ProgrammeChecklistDTO(
        id = cID,
        type = ProgrammeChecklistTypeDTO.CONTRACTING,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
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
                secondOption = "no",
                justification = ""
            )
        ),
        ProgrammeChecklistComponent(
            3L,
            ProgrammeChecklistComponentType.TEXT_INPUT,
            3,
            TextInputMetadata(
                "question 2",
                "Explanation",
                1000
            )
        ),
        ProgrammeChecklistComponent(
            4L,
            ProgrammeChecklistComponentType.SCORE,
            4,
            ScoreMetadata(
                "question",
                BigDecimal(1),
            )
        ),
    )

    private val checklistDetail = ProgrammeChecklistDetail(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
        components = components
    )
    private val contractingChecklistDetail = ProgrammeChecklistDetail(
        id = cID,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
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
                "no",
                justification = ""
            )
        ),
        ProgrammeChecklistComponentDTO(
            3L,
            ProgrammeChecklistComponentTypeDTO.TEXT_INPUT,
            3,
            TextInputMetadataDTO(
                "question 2",
                "Explanation",
                1000
            )
        ),
        ProgrammeChecklistComponentDTO(
            4L,
            ProgrammeChecklistComponentTypeDTO.SCORE,
            4,
            ScoreMetadataDTO(
                "question",
                BigDecimal(1)
            )
        )
    )

    private val checklistDetailDTO = ProgrammeChecklistDetailDTO(
        id = ID,
        type = ProgrammeChecklistTypeDTO.APPLICATION_FORM_ASSESSMENT,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        components = componentsDTO
    )

    private val contractingChecklistDetailDTO = ProgrammeChecklistDetailDTO(
        id = cID,
        type = ProgrammeChecklistTypeDTO.CONTRACTING,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
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
    lateinit var cloneInteractor: CloneProgrammeChecklistInteractor

    @MockK
    lateinit var getChecklistDetailInteractor: GetProgrammeChecklistDetailInteractor

    @MockK
    lateinit var deleteInteractor: DeleteProgrammeChecklistInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeChecklistController

    @Test
    fun `get checklists`() {
        every { getChecklistInteractor.getProgrammeChecklist(Sort.unsorted()) } returns listOf(checklist, contractingChecklist)
        assertThat(controller.getProgrammeChecklists(Pageable.unpaged()).subList(0, 2))
            .usingRecursiveComparison()
            .isEqualTo(listOf(checklistDTO, contractingChecklistDTO))
    }

    @Test
    fun `get checklist details`() {
        every { getChecklistDetailInteractor.getProgrammeChecklistDetail(ID) } returns checklistDetail
        assertThat(controller.getProgrammeChecklistDetail(ID))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `get contracting checklist details`() {
        every { getChecklistDetailInteractor.getProgrammeChecklistDetail(cID) } returns contractingChecklistDetail
        assertThat(controller.getProgrammeChecklistDetail(cID))
            .usingRecursiveComparison()
            .isEqualTo(contractingChecklistDetailDTO)
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
    fun `clone checklist`() {
        every { cloneInteractor.clone(ID) } returns checklistDetail
        assertThat(controller.cloneProgrammeChecklist(ID))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `create contracting checklist`() {
        val checklistSlot = slot<ProgrammeChecklistDetail>()
        every { createInteractor.create(capture(checklistSlot)) } returnsArgument 0
        assertThat(controller.createProgrammeChecklist(contractingChecklistDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(contractingChecklistDetailDTO)
    }

    @Test
    fun `update checklist details`() {
        val checklistSlot = slot<ProgrammeChecklistDetail>()
        every { updateInteractor.update(capture(checklistSlot)) } returnsArgument 0
        assertThat(controller.updateProgrammeChecklist(checklistDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `update contracting checklist details`() {
        val checklistSlot = slot<ProgrammeChecklistDetail>()
        every { updateInteractor.update(capture(checklistSlot)) } returnsArgument 0
        assertThat(controller.updateProgrammeChecklist(contractingChecklistDetailDTO))
            .usingRecursiveComparison()
            .isEqualTo(contractingChecklistDetailDTO)
    }

    @Test
    fun `delete checklist`() {
        every { deleteInteractor.deleteProgrammeChecklist(ID) } just Runs
        assertDoesNotThrow { deleteInteractor.deleteProgrammeChecklist(ID) }
    }

    @Test
    fun `delete contracting checklist`() {
        every { deleteInteractor.deleteProgrammeChecklist(cID) } just Runs
        assertDoesNotThrow { deleteInteractor.deleteProgrammeChecklist(cID) }
    }
}
