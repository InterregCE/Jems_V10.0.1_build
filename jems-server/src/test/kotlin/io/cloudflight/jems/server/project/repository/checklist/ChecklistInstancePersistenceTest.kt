package io.cloudflight.jems.server.project.repository.checklist

import com.querydsl.core.types.Predicate
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceEntity
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceId
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.service.checklist.model.*
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.update.UpdateChecklistInstanceStatusNotFinishedException
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChecklistInstancePersistenceTest : UnitTest() {

    private val ID = 1L
    private val RELATED_TO_ID = 2L
    private val CREATOR_ID = 3L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val checkLisDetail = ChecklistInstanceDetail(
        id = ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = RELATED_TO_ID,
        creatorEmail = "test@email.com",
        finishedDate = null,
        consolidated = false,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                HeadlineMetadata("headline"),
                HeadlineInstanceMetadata()
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe"),
                OptionsToggleInstanceMetadata("yes")
            ),
            ChecklistComponentInstance(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                TextInputMetadata("Question to be answered", "Label", 2000),
                TextInputInstanceMetadata("Explanation")
            )
        )
    )

    private val createdCheckLisDetail = ChecklistInstanceDetail(
        id = 0,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        consolidated = false,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                HeadlineMetadata("headline"),
                null
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe"),
                null
            ),
            ChecklistComponentInstance(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                TextInputMetadata("Question to be answered", "Label", 2000),
                null
            )
        )
    )

    private val createChecklist = CreateChecklistInstanceModel(
        RELATED_TO_ID,
        PROGRAMME_CHECKLIST_ID
    )

    private val programmeChecklist = ProgrammeChecklistEntity(
        id = PROGRAMME_CHECKLIST_ID,
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

    private val user = UserEntity(
        id = CREATOR_ID,
        email = "test@email.com",
        name = "name",
        surname = "surname",
        userRole = UserRoleEntity(2L, "name"),
        password = "pass",
        userStatus = UserStatus.ACTIVE
    )

    private fun checkListEntity(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) = ChecklistInstanceEntity(
        id = ID,
        status = status,
        finishedDate = null,
        relatedToId = RELATED_TO_ID,
        programmeChecklist = programmeChecklist,
        creator = user,
        components = mutableSetOf(
            ChecklistComponentInstanceEntity(
                ChecklistComponentInstanceId(2L),
                ProgrammeChecklistComponentEntity(
                    2L,
                    ProgrammeChecklistComponentType.HEADLINE,
                    1,
                    null,
                    File(this::class.java.classLoader.getResource("headline.json").file).readText()
                ),
                "{}"
            ),
            ChecklistComponentInstanceEntity(
                ChecklistComponentInstanceId(3L),
                ProgrammeChecklistComponentEntity(
                    3L,
                    ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                    2,
                    null,
                    File(this::class.java.classLoader.getResource("options_toggle.json").file).readText()
                ),
                "{\"answer\":\"yes\"}"
            ),
            ChecklistComponentInstanceEntity(
                ChecklistComponentInstanceId(4L),
                ProgrammeChecklistComponentEntity(
                    4L,
                    ProgrammeChecklistComponentType.TEXT_INPUT,
                    3,
                    null,
                    File(this::class.java.classLoader.getResource("text_input.json").file).readText()
                ),
                "{\"explanation\":\"Explanation\"}"
            )
        )
    )


    @RelaxedMockK
    lateinit var repository: ChecklistInstanceRepository

    @MockK
    lateinit var userRepo: UserRepository

    @MockK
    lateinit var programmeChecklistRepository: ProgrammeChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ChecklistInstancePersistenceProvider

    @Test
    fun `find checklists`() {

        val predicate = slot<Predicate>()
        persistence.findChecklistInstances(ChecklistInstanceSearchRequest(
            relatedToId = 1,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            status = ChecklistInstanceStatus.FINISHED,
            visible = true,
        ))

        verify { repository.findAll(capture(predicate)) }
        assertThat(predicate.captured.toString()).isEqualTo(
            "checklistInstanceEntity.relatedToId = 1 " +
                "&& checklistInstanceEntity.programmeChecklist.type = APPLICATION_FORM_ASSESSMENT " +
                "&& checklistInstanceEntity.status = FINISHED " +
                "&& checklistInstanceEntity.visible = true"
        )
    }

    @Test
    fun `consolidate checklist`() {
        val checkListEntity = checkListEntity()
        every { repository.findById(ID) } returns Optional.of(checkListEntity)

        persistence.consolidateChecklistInstance(ID, true)

        assertThat(checkListEntity.consolidated).isTrue
    }

    @Test
    fun `change status to FINISHED`() {
        val checkListEntity = checkListEntity()
        every { repository.findById(ID) } returns Optional.of(checkListEntity)

        persistence.changeStatus(ID, ChecklistInstanceStatus.FINISHED)

        assertThat(checkListEntity.status).isEqualTo(ChecklistInstanceStatus.FINISHED)
        assertThat(checkListEntity.finishedDate).isNotNull
    }

    @Test
    fun `change status to DRAFT`() {
        val checkListEntity = checkListEntity()
        checkListEntity.visible = true
        every { repository.findById(ID) } returns Optional.of(checkListEntity)

        persistence.changeStatus(ID, ChecklistInstanceStatus.DRAFT)

        assertThat(checkListEntity.status).isEqualTo(ChecklistInstanceStatus.DRAFT)
        assertThat(checkListEntity.finishedDate).isNull()
        assertThat(checkListEntity.visible).isFalse
    }

    @Test
    fun getChecklistDetail() {
        val optionalCheckList = Optional.of(checkListEntity())
        every { repository.findById(ID) } returns optionalCheckList
        assertThat(persistence.getChecklistDetail(ID))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun create() {
        val checklistSlot = slot<ChecklistInstanceEntity>()
        every { repository.save(capture(checklistSlot)) } returnsArgument 0
        every { programmeChecklistRepository.getById(PROGRAMME_CHECKLIST_ID) } returns programmeChecklist
        every { userRepo.getById(CREATOR_ID) } returns user
        assertThat(persistence.create(createChecklist, CREATOR_ID))
            .usingRecursiveComparison()
            .isEqualTo(createdCheckLisDetail)
    }

    @Test
    fun update() {
        val optionalCheckList = Optional.of(checkListEntity())
        every { repository.findById(ID) } returns optionalCheckList
        assertThat(persistence.update(checkLisDetail))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun delete() {
        val optionalCheckList = Optional.of(checkListEntity())
        every { repository.findById(ID) } returns optionalCheckList
        val entitySentToDelete = slot<ChecklistInstanceEntity>()
        every { repository.delete(capture(entitySentToDelete)) } answers { }
        persistence.deleteById(ID)
        assertThat(entitySentToDelete.captured.id).isEqualTo(ID)
    }

    @Test
    fun `update selection - not finished exception`() {
        every { repository.findAllById(any()) } returns listOf(checkListEntity())

        assertThrows<UpdateChecklistInstanceStatusNotFinishedException> {
            persistence.updateSelection(mapOf(ID to true))
        }
    }

}
