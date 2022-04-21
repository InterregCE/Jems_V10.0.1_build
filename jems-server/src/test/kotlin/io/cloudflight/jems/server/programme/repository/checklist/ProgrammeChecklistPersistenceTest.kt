package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
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
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault())
    )

    private val checkLisDetail = ProgrammeChecklistDetail(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
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
            )
        )
    )

    private val checkListEntity = ProgrammeChecklistEntity(
        id = ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
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
            )
        )
    )


    @MockK
    lateinit var repository: ProgrammeChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ProgrammeChecklistPersistenceProvider

    @Test
    fun getMax100Checklists() {
        every { repository.findTop100ByOrderById() } returns listOf(checkListEntity)
        assertThat(persistence.getMax100Checklists().get(0))
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
