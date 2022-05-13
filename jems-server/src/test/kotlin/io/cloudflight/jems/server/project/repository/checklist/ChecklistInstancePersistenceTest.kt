package io.cloudflight.jems.server.project.repository.checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceEntity
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceId
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChecklistInstancePersistenceTest : UnitTest() {

    private val ID = 1L
    private val RELATED_TO_ID = 2L
    private val CREATOR_ID = 3L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val checkList = ChecklistInstance(
        id = ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID
    )

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

    private val checkListEntity = ChecklistInstanceEntity(
        id = ID,
        status = ChecklistInstanceStatus.DRAFT,
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


    @MockK
    lateinit var repository: ChecklistInstanceRepository

    @MockK
    lateinit var userRepo: UserRepository

    @MockK
    lateinit var programmeChecklistRepository: ProgrammeChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ChecklistInstancePersistenceProvider

    @Test
    fun getChecklistsByRelationAndCreatorAndType() {
        every {
            repository.findByRelatedToIdAndCreatorIdAndProgrammeChecklistType(
                RELATED_TO_ID, CREATOR_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        } returns listOf(checkListEntity)
        assertThat(
            persistence.getChecklistsByRelationAndCreatorAndType(
                RELATED_TO_ID, CREATOR_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            ).get(0)
        )
            .usingRecursiveComparison()
            .isEqualTo(checkList)
    }

    @Test
    fun `get checklists by related id and type`() {
        every {
            repository.findByRelatedToIdAndProgrammeChecklistType(RELATED_TO_ID, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)
        } returns listOf(checkListEntity)

        persistence.getChecklistsByRelatedIdAndType(RELATED_TO_ID, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

        verify { repository.findByRelatedToIdAndProgrammeChecklistType(RELATED_TO_ID, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT) }
    }

    @Test
    fun `consolidate checklist`() {
        every { repository.findById(ID) } returns Optional.of(checkListEntity)

        persistence.consolidateChecklistInstance(ID, true)

        assertThat(checkListEntity.consolidated).isTrue
    }

    @Test
    fun `change status to FINISHED`() {
        every { repository.findById(ID) } returns Optional.of(checkListEntity)

        persistence.changeStatus(ID, ChecklistInstanceStatus.FINISHED)

        assertThat(checkListEntity.status).isEqualTo(ChecklistInstanceStatus.FINISHED)
        assertThat(checkListEntity.finishedDate).isNotNull
    }

    @Test
    fun `change status to DRAFT`() {
        every { repository.findById(ID) } returns Optional.of(checkListEntity)

        persistence.changeStatus(ID, ChecklistInstanceStatus.DRAFT)

        assertThat(checkListEntity.status).isEqualTo(ChecklistInstanceStatus.DRAFT)
        assertThat(checkListEntity.finishedDate).isNull()
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
        val optionalCheckList = Optional.of(checkListEntity)
        every { repository.findById(ID) } returns optionalCheckList
        assertThat(persistence.update(checkLisDetail))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun delete() {
        val optionalCheckList = Optional.of(checkListEntity)
        every { repository.findById(ID) } returns optionalCheckList
        val entitySentToDelete = slot<ChecklistInstanceEntity>()
        every { repository.delete(capture(entitySentToDelete)) } answers { }
        persistence.deleteById(ID)
        assertThat(entitySentToDelete.captured.id).isEqualTo(ID)
    }

}
