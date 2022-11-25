package io.cloudflight.jems.server.project.repository.checklist.contracting

import com.querydsl.core.types.Predicate
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.repository.checklist.ChecklistInstanceRepository
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

class ContractingChecklistInstancePersistenceTest : UnitTest() {

    private val projectId = 1L
    private val creatorId = 2L
    private val programmeChecklistId = 3L

    private val createdContractingChecklistDetail = ChecklistInstanceDetail(
        id = 0,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "test@email.com",
        creatorId = creatorId,
        relatedToId = projectId,
        finishedDate = null,
        consolidated = false,
        visible = false,
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

    private val createContractingChecklist = CreateChecklistInstanceModel(
        projectId,
        programmeChecklistId
    )

    private val programmeChecklist = ProgrammeChecklistEntity(
        id = programmeChecklistId,
        type = ProgrammeChecklistType.CONTRACTING,
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
        id = creatorId,
        email = "test@email.com",
        name = "name",
        surname = "surname",
        userRole = UserRoleEntity(2L, "name"),
        password = "pass",
        userStatus = UserStatus.ACTIVE
    )

    @RelaxedMockK
    lateinit var repository: ChecklistInstanceRepository

    @MockK
    lateinit var userRepo: UserRepository

    @MockK
    lateinit var programmeChecklistRepository: ProgrammeChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ContractingChecklistInstancePersistenceProvider

    @Test
    fun `find contracting checklists`() {
        val predicate = slot<Predicate>()
        persistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
                relatedToId = 1,
                type = ProgrammeChecklistType.CONTRACTING,
                status = ChecklistInstanceStatus.FINISHED,
                visible = true,
            )
        )

        verify { repository.findAll(capture(predicate)) }
        Assertions.assertThat(predicate.captured.toString()).isEqualTo(
            "checklistInstanceEntity.relatedToId = 1 " +
                    "&& checklistInstanceEntity.programmeChecklist.type = CONTRACTING " +
                    "&& checklistInstanceEntity.status = FINISHED " +
                    "&& checklistInstanceEntity.visible = true"
        )
    }

    @Test
    fun create() {
        val checklistSlot = slot<ChecklistInstanceEntity>()
        every { repository.save(capture(checklistSlot)) } returnsArgument 0
        every { programmeChecklistRepository.getById(programmeChecklistId) } returns programmeChecklist
        every { userRepo.getById(creatorId) } returns user
        Assertions.assertThat(persistence.create(createContractingChecklist, creatorId, projectId))
            .usingRecursiveComparison()
            .isEqualTo(createdContractingChecklistDetail)
    }
}
