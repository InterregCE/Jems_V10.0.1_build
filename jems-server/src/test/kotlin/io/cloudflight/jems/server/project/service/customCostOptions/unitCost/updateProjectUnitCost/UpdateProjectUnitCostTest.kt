package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.updateProjectUnitCost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

internal class UpdateProjectUnitCostTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 22L

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "CUST_ID",
            callName = "",
            acronym = "PROJ_ACR",
            status = ApplicationStatus.DRAFT,
        )

        private val oldUnitCost = ProgrammeUnitCost(
            id = 17L,
            projectId = PROJECT_ID,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN")),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type EN")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
        )

        private val unitCostNew = ProgrammeUnitCost(
            id = 17L,
            projectId = PROJECT_ID,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN new")),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN new")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type EN new")),
            costPerUnit = BigDecimal.TEN,
            isOneCostCategory = false,
            categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
        )

        val oldStaffCost = BudgetStaffCostEntry(
            id = 97645L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.ONE,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "old to be removed EN")),
        )

        val newStaffCost = BudgetStaffCostEntry(
            id = 97645L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.valueOf(1000, 2),
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.TEN,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "type EN new")),
        )

        val oldEquipmentCost = BudgetGeneralCostEntry(
            id = 98245L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.ONE,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "old to be removed EN")),
        )

        val newEquipmentCost = BudgetGeneralCostEntry(
            id = 98245L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.valueOf(1000, 2),
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.TEN,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "type EN new")),
        )

        val oldExternalCost = BudgetGeneralCostEntry(
            id = 97745L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.ONE,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "old to be removed EN")),
        )

        val newExternalCost = BudgetGeneralCostEntry(
            id = 97745L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.valueOf(1000, 2),
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.TEN,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "type EN new")),
        )

        val oldInfraCost = BudgetGeneralCostEntry(
            id = 97825L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.ONE,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "old to be removed EN")),
        )

        val newInfraCost = BudgetGeneralCostEntry(
            id = 97825L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.valueOf(1000, 2),
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.TEN,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "type EN new")),
        )

        val oldTravelCost = BudgetTravelAndAccommodationCostEntry(
            id = 97836L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.ONE,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "old to be removed EN")),
        )

        val newTravelCost = BudgetTravelAndAccommodationCostEntry(
            id = 97836L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.valueOf(1000, 2),
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
            pricePerUnit = BigDecimal.TEN,
            description = emptySet(),
            comments = emptySet(),
            unitType = setOf(InputTranslation(SystemLanguage.EN, "type EN new")),
        )

        val oldMultiCost = BudgetUnitCostEntry(
            id = 97836L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
        )

        val newMultiCost = BudgetUnitCostEntry(
            id = 97836L,
            numberOfUnits = BigDecimal.ONE,
            rowSum = BigDecimal.valueOf(1000, 2),
            budgetPeriods = mutableSetOf(),
            unitCostId = 17L,
        )

        private val auditProject = AuditProject(
            id = "22",
            customIdentifier = "CUST_ID",
            name = "PROJ_ACR",
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
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var projectPartnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence
    @MockK
    lateinit var projectPartnerBudgetCostsUpdatePersistence: ProjectPartnerBudgetCostsUpdatePersistence

    @InjectMockKs
    lateinit var interactor: UpdateProjectUnitCost

    @BeforeEach
    fun resetMocks() {
        clearMocks(programmeUnitCostPersistence, projectUnitCostPersistence, projectPersistence, auditPublisher,
            generalValidator, partnerPersistence, projectPartnerBudgetCostsPersistence, projectPartnerBudgetCostsUpdatePersistence)

        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<Set<InputTranslation>>(), any(), any()) } returns emptyMap()
    }

    @Test
    fun updateProjectUnitCost() {
        every { projectUnitCostPersistence.getProjectUnitCost(PROJECT_ID, unitCostId = 17L) } returns oldUnitCost.copy()

        val partnerId = 190L
        val partner = mockk<ProjectPartnerDetail>()
        every { partner.id } returns partnerId
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(partner)

        mockBudgetUpdates(partnerId)

        every { programmeUnitCostPersistence.updateUnitCost(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        assertThat(interactor.updateProjectUnitCost(PROJECT_ID, unitCostNew.copy(projectId = null))).isEqualTo(unitCostNew)
        verify(exactly = 1) { programmeUnitCostPersistence.updateUnitCost(unitCostNew) }

        verifyBudgetUpdates(partnerId)

        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROGRAMME_UNIT_COST_CHANGED)
        assertThat(auditSlot.captured.auditCandidate.project).isEqualTo(auditProject)
    }

    private fun mockBudgetUpdates(partnerId: Long) {
        every { projectPartnerBudgetCostsPersistence.getBudgetStaffCosts(partnerId) } returns listOf(oldStaffCost)
        every { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetStaffCosts(PROJECT_ID, partnerId, any())
        } returnsArgument 2

        every { projectPartnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerId) } returns listOf(oldEquipmentCost)
        every { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetEquipmentCosts(PROJECT_ID, partnerId, any())
        } returnsArgument 2

        every { projectPartnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) } returns listOf(oldExternalCost)
        every { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetExternalExpertiseAndServicesCosts(PROJECT_ID, partnerId, any())
        } returnsArgument 2

        every { projectPartnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId) } returns listOf(oldInfraCost)
        every { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetInfrastructureAndWorksCosts(PROJECT_ID, partnerId, any())
        } returnsArgument 2

        every { projectPartnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId) } returns listOf(oldTravelCost)
        every { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetTravelAndAccommodationCosts(PROJECT_ID, partnerId, any())
        } returnsArgument 2

        every { projectPartnerBudgetCostsPersistence.getBudgetUnitCosts(partnerId) } returns listOf(oldMultiCost)
        every { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetUnitCosts(PROJECT_ID, partnerId, any())
        } returnsArgument 2
    }

    private fun verifyBudgetUpdates(partnerId: Long) {
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetStaffCosts(PROJECT_ID, partnerId, listOf(newStaffCost.copy()))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetEquipmentCosts(PROJECT_ID, partnerId, listOf(newEquipmentCost.copy()))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetExternalExpertiseAndServicesCosts(PROJECT_ID, partnerId, listOf(newExternalCost.copy()))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetInfrastructureAndWorksCosts(PROJECT_ID, partnerId, listOf(newInfraCost.copy()))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetTravelAndAccommodationCosts(PROJECT_ID, partnerId, listOf(newTravelCost.copy()))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetUnitCosts(PROJECT_ID, partnerId, listOf(newMultiCost.copy()))
        }
    }

    @Test
    fun `updateProjectUnitCost - deselected categories`() {
        every { projectUnitCostPersistence.getProjectUnitCost(PROJECT_ID, unitCostId = 17L) } returns
            oldUnitCost.copy(categories = BudgetCategory.values().toHashSet())

        val partnerId = 195L
        val partner = mockk<ProjectPartnerDetail>()
        every { partner.id } returns partnerId
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(partner)

        mockBudgetUpdates(partnerId)

        every { programmeUnitCostPersistence.updateUnitCost(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        every { auditPublisher.publishEvent(any()) } answers { }

        val unitCostToBeSaved = unitCostNew.copy(
            projectId = null,
            categories = setOf(BudgetCategory.StaffCosts, BudgetCategory.EquipmentCosts),
        )
        assertThat(interactor.updateProjectUnitCost(PROJECT_ID, unitCostToBeSaved)).isEqualTo(unitCostToBeSaved.copy())

        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetExternalExpertiseAndServicesCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetInfrastructureAndWorksCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetTravelAndAccommodationCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `updateProjectUnitCost - changed to single-category`() {
        every { projectUnitCostPersistence.getProjectUnitCost(PROJECT_ID, unitCostId = 17L) } returns oldUnitCost.copy()

        val partnerId = 199L
        val partner = mockk<ProjectPartnerDetail>()
        every { partner.id } returns partnerId
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(partner)

        mockBudgetUpdates(partnerId)

        every { programmeUnitCostPersistence.updateUnitCost(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        every { auditPublisher.publishEvent(any()) } answers { }

        val unitCostToBeSaved = unitCostNew.copy(
            projectId = null,
            isOneCostCategory = true,
            categories = setOf(BudgetCategory.InfrastructureCosts),
        )
        assertThat(interactor.updateProjectUnitCost(PROJECT_ID, unitCostToBeSaved)).isEqualTo(unitCostToBeSaved.copy())

        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetStaffCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetEquipmentCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetExternalExpertiseAndServicesCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetInfrastructureAndWorksCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetTravelAndAccommodationCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { projectPartnerBudgetCostsUpdatePersistence
            .createOrUpdateBudgetUnitCosts(PROJECT_ID, partnerId, listOf(/* empty */))
        }
        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `updateProjectUnitCost - no changes`() {
        every { projectUnitCostPersistence.getProjectUnitCost(PROJECT_ID, unitCostId = 17L) } returns oldUnitCost.copy()

        val partnerId = 203L
        val partner = mockk<ProjectPartnerDetail>()
        every { partner.id } returns partnerId
        every { partnerPersistence.findTop30ByProjectId(PROJECT_ID) } returns listOf(partner)

        every { programmeUnitCostPersistence.updateUnitCost(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        every { auditPublisher.publishEvent(any()) } answers { }

        assertThat(interactor.updateProjectUnitCost(PROJECT_ID, oldUnitCost.copy(projectId = null))).isEqualTo(oldUnitCost.copy())
        verify(exactly = 1) { programmeUnitCostPersistence.updateUnitCost(oldUnitCost) }

        verify(exactly = 0) { partnerPersistence.findTop30ByProjectId(any()) }

        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
    }

}
