package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.model.*
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProgrammeChecklistPersistenceTest : UnitTest() {

    private val ID = 1L

    private val checkList = ProgrammeChecklist(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
    )

    private val checkLisDetail = ProgrammeChecklistDetail(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
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
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
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

    @MockK
    lateinit var repository: ProgrammeChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ProgrammeChecklistPersistenceProvider

    @Test
    fun getMax100Checklists() {
        val programmeChecklistRow: ProgrammeChecklistRow = mockk()
        every { programmeChecklistRow.id } returns ID
        every { programmeChecklistRow.type } returns ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
        every { programmeChecklistRow.name } returns "name"
        every { programmeChecklistRow.minScore } returns BigDecimal(0)
        every { programmeChecklistRow.maxScore } returns BigDecimal(10)
        every { programmeChecklistRow.allowsDecimalScore } returns false
        every { programmeChecklistRow.lastModificationDate } returns ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault())
        every { programmeChecklistRow.count } returns 0

        every { repository.findTop100ByOrderById() } returns listOf(programmeChecklistRow)
        assertThat(persistence.getMax100Checklists()[0])
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
    fun createOrUpdate() {
        val checklistSlot = slot<ProgrammeChecklistEntity>()
        every { repository.save(capture(checklistSlot)) } returnsArgument 0
        assertThat(persistence.createOrUpdate(checkLisDetail))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

}
