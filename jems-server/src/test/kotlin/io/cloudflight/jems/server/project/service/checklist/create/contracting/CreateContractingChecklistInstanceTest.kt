package io.cloudflight.jems.server.project.service.checklist.create.contracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.service.checklist.ContractingChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.math.BigDecimal

internal class CreateContractingChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val projectId = 5L

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
    lateinit var persistence: ContractingChecklistInstancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var createContractingChecklistInstance: CreateContractingChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
    }

    @Test
    fun `create contracting checklist - OK`() {
        val currentUser = LocalCurrentUser(
            AuthorizationUtil.userApplicant, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.CallRetrieve.key))
        )
        every { securityService.currentUser } returns currentUser
        every {
            persistence.create(
                createContractingChecklist,
                creatorId,
                projectId
            )
        } returns createdContractingChecklistDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        Assertions.assertThat(createContractingChecklistInstance.create(projectId, createContractingChecklist))
            .usingRecursiveComparison()
            .isEqualTo(createdContractingChecklistDetail)
    }
}
