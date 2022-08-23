package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.createProjectUnitCost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

internal class CreateProjectUnitCostTest : UnitTest() {

    companion object {

        private fun projectSummary(id: Long) = ProjectSummary(
            id = id,
            customIdentifier = "CUST_ID",
            callName = "",
            acronym = "PROJ_ACR",
            status = ApplicationStatus.DRAFT,
        )

        private fun auditProject(id: Long) = AuditProject(
            id = id.toString(),
            customIdentifier = "CUST_ID",
            name = "PROJ_ACR",
        )

        private val createUnitCost = ProgrammeUnitCost(
            id = 0L,
            projectId = null,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type uc 1")),
            justification = setOf(InputTranslation(SystemLanguage.EN, "justification uc 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(BudgetCategory.StaffCosts, BudgetCategory.OfficeAndAdministrationCosts),
        )
    }

    @MockK
    lateinit var programmeUnitCostPersistence: ProgrammeUnitCostPersistence
    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher
    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: CreateProjectUnitCost

    @BeforeEach
    fun resetMocks() {
        clearMocks(programmeUnitCostPersistence, projectUnitCostPersistence, projectPersistence, auditPublisher, generalValidator)

        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<Set<InputTranslation>>(), any(), any()) } returns emptyMap()
    }

    @Test
    fun createProjectUnitCost() {
        val projectId = 75L
        every { projectUnitCostPersistence.getCount(projectId) } returns 4L
        every { programmeUnitCostPersistence.createUnitCost(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary(projectId)

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        assertThat(interactor.createProjectUnitCost(projectId, createUnitCost))
            .isEqualTo(createUnitCost.copy(projectId = projectId))

        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROGRAMME_UNIT_COST_ADDED)
        assertThat(auditSlot.captured.auditCandidate.project).isEqualTo(auditProject(projectId))
    }

    @Test
    fun `createProjectUnitCost - wrong inputs`() {
        val projectId = 77L
        val validationSlot = mutableListOf<Map<String, I18nMessage>?>()
        every { generalValidator.throwIfAnyIsInvalid(*varargAllNullable { validationSlot.add(it) }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<Set<InputTranslation>>(), any(), any()) } answers {
            mapOf(thirdArg<String>()
                to I18nMessage(i18nKey = "${firstArg<Set<InputTranslation>>().joinToString(",")}---${secondArg<Int>()}")
            )
        }

        assertThrows<AppInputValidationException> { interactor.createProjectUnitCost(projectId, createUnitCost) }
        assertThat(validationSlot).containsExactly(
            mapOf("name" to I18nMessage("EN=UC1---50")),
            mapOf("description" to I18nMessage("EN=desc unit cost 1---255")),
            mapOf("type" to I18nMessage("EN=type uc 1---25")),
            mapOf("justification" to I18nMessage("EN=justification uc 1---5000")),
        )
    }

    @Test
    fun `createProjectUnitCost - wrong id`() {
        val projectId = 80L
        every { projectUnitCostPersistence.getCount(projectId) } returns 0L

        assertThrows<I18nValidationException> {
            interactor.createProjectUnitCost(projectId, createUnitCost.copy(id = 7L))
        }
    }

    @Test
    fun `createProjectUnitCost - max amount reached`() {
        val projectId = 81L
        every { projectUnitCostPersistence.getCount(projectId) } returns 10L

        assertThrows<I18nValidationException> {
            interactor.createProjectUnitCost(projectId, createUnitCost)
        }
    }

}
