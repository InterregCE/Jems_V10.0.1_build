package io.cloudflight.jems.server.project.service.checklist.clone.contracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class CloneContractingChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val projectId = 5L
    private val TODAY = ZonedDateTime.now()

    private val createContractingChecklist = CreateChecklistInstanceModel(
        projectId,
        programmeChecklistId
    )

    private val createdContractingChecklistDetail = ChecklistInstanceDetail(
        checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        relatedToId = projectId,
        creatorEmail = "a@a",
        creatorId = creatorId,
        createdAt = TODAY,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = true,
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
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe", ""),
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

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var cloneContractingChecklistInstance: CloneContractingChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        AppInputValidationException(emptyMap())
    }

    @Test
    fun `clone contracting checklist - OK`() {
        every { securityService.getUserIdOrThrow() } returns AuthorizationUtil.userApplicant.id
        every { persistence.getChecklistDetail(checklistId) } returns createdContractingChecklistDetail
        every { persistence.create(createContractingChecklist, creatorId) } returns createdContractingChecklistDetail
        every { persistence.update(any()) } returns createdContractingChecklistDetail

        Assertions.assertThat(cloneContractingChecklistInstance.clone(projectId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(createdContractingChecklistDetail)
    }
}
