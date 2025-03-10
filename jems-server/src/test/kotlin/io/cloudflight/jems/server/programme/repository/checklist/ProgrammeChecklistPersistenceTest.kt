package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.entity.CallSelectedChecklistEntity
import io.cloudflight.jems.server.call.entity.CallSelectedChecklistId
import io.cloudflight.jems.server.call.repository.CallSelectedChecklistRepository
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistRow
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import java.io.File
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProgrammeChecklistPersistenceTest : UnitTest() {

    private val ID = 1L

    private val checkList = ProgrammeChecklist(
        id = ID,
        type = APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
    )

    private val checkLisDetail = ProgrammeChecklistDetail(
        id = ID,
        type = APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
        components = mutableListOf(
            ProgrammeChecklistComponent(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                HeadlineMetadata("headline")
            ),
            ProgrammeChecklistComponent(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe")
            ),
            ProgrammeChecklistComponent(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                TextInputMetadata("Question to be answered", "Label", 2000)
            )
        )
    )

    private val checkListEntity = ProgrammeChecklistEntity(
        id = ID,
        type = APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        components = mutableSetOf(
            ProgrammeChecklistComponentEntity(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                null,
                File(this::class.java.classLoader.getResource("headline.json").file).readText()
            ),
            ProgrammeChecklistComponentEntity(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                null,
                File(this::class.java.classLoader.getResource("options_toggle.json").file).readText()
            ),
            ProgrammeChecklistComponentEntity(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                null,
                File(this::class.java.classLoader.getResource("text_input.json").file).readText()
            )
        )
    )

    private val callSelectedChecklistEntity = CallSelectedChecklistEntity(
        id = CallSelectedChecklistId(
            call = mockk { every { id } returns 10L },
            programmeChecklist = checkListEntity
        )
    )

    @MockK
    lateinit var repository: ProgrammeChecklistRepository

    @MockK
    lateinit var callSelectedChecklistRepository: CallSelectedChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ProgrammeChecklistPersistenceProvider

    @Test
    fun getMax100Checklists() {
        val programmeChecklistRow: ProgrammeChecklistRow = mockk()
        every { programmeChecklistRow.id } returns ID
        every { programmeChecklistRow.type } returns APPLICATION_FORM_ASSESSMENT
        every { programmeChecklistRow.name } returns "name"
        every { programmeChecklistRow.minScore } returns BigDecimal(0)
        every { programmeChecklistRow.maxScore } returns BigDecimal(10)
        every { programmeChecklistRow.allowsDecimalScore } returns false
        every { programmeChecklistRow.lastModificationDate } returns ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault())
        every { programmeChecklistRow.instancesCount } returns 0

        every { repository.findTop100ByOrderByIdDesc(Sort.unsorted()) } returns listOf(programmeChecklistRow)
        assertThat(persistence.getMax100Checklists(Sort.unsorted())[0])
            .usingRecursiveComparison()
            .isEqualTo(checkList)
    }

    @Test
    fun getChecklistDetail() {
        val optionalCheckList = Optional.of(checkListEntity)
        every { repository.findById(ID) } returns optionalCheckList
        assertThat(persistence.getChecklistDetail(ID))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun saveChecklist() {
        val checklistSlot = slot<ProgrammeChecklistEntity>()
        every { repository.save(capture(checklistSlot)) } returnsArgument 0
        assertThat(persistence.createChecklist(checkLisDetail))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun getChecklistsByTypeAndCall() {
        val callId = 10L
        val type = APPLICATION_FORM_ASSESSMENT
        every { callSelectedChecklistRepository.findAllByIdCallIdAndIdProgrammeChecklistType(callId, type) } returns
                listOf(callSelectedChecklistEntity)

        assertThat(persistence.getChecklistsByTypeAndCall(type, callId))
            .isEqualTo(
                callSelectedChecklistEntity.id.programmeChecklist.let { checkList ->
                    listOf(IdNamePair(checkList.id, checkList.name!!))
                }
            )
    }

}
