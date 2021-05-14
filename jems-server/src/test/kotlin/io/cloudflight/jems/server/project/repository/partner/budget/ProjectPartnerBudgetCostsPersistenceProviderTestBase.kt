package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralBase
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralRow
import io.cloudflight.jems.server.project.entity.partner.budget.general.equipment.ProjectPartnerBudgetEquipmentEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructureEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostRow
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.reflect.KClass


open class ProjectPartnerBudgetCostsPersistenceProviderTestBase : UnitTest() {

    protected val partnerId = 1L
    protected val projectId = 2L
    protected val timestamp: Timestamp = Timestamp.valueOf(LocalDateTime.now())
    protected val version = 3

    private val entityId = 1L
    private val unitCostId = 5L
    private val investmentId = 4L
    private val periodNumber = 2
    private val description = "description"
    private val awardProcedures = "award procedures"
    private val unitType = "unit type"

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository

    @RelaxedMockK
    lateinit var budgetTravelRepository: ProjectPartnerBudgetTravelRepository

    @RelaxedMockK
    lateinit var budgetExternalRepository: ProjectPartnerBudgetExternalRepository

    @RelaxedMockK
    lateinit var budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository

    @RelaxedMockK
    lateinit var budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository

    @RelaxedMockK
    lateinit var budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository

    @RelaxedMockK
    lateinit var budgetLumpSumRepository: ProjectLumpSumRepository

    protected lateinit var persistence: ProjectPartnerBudgetCostsPersistenceProvider

    @BeforeAll
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPartnerBudgetCostsPersistenceProvider(
            projectVersionUtils,
            projectPersistence,
            budgetStaffCostRepository,
            budgetTravelRepository,
            budgetExternalRepository,
            budgetEquipmentRepository,
            budgetInfrastructureRepository,
            budgetUnitCostRepository,
            budgetLumpSumRepository
        )
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
        every { projectVersionRepo.findTimestampByVersion(projectId, version) } returns timestamp
    }

    protected class CurrentVersionOfBudgetCostTestInput<T : ProjectPartnerBudgetBase, E>(
        val name: String,
        var entity: T,
        val repository: ProjectPartnerBaseBudgetRepository<T>,
        val callback: (partnerId: Long, version: Int?) -> List<E>,
        val expectedResult: E
    )

    protected class PreviousVersionOfBudgetCostTestInput<T : ProjectPartnerBudgetBase, E, R>(
        val name: String,
        var row: R,
        val repository: ProjectPartnerBaseBudgetRepository<T>,
        val callback: (partnerId: Long, version: Int?) -> List<E>,
        val expectedResult: E,
        val projectClass: KClass<*> = ProjectPartnerBudgetStaffCostRow::class,
        val isForGettingUnitCosts: Boolean = false
    )


    protected fun testInputsForGettingCurrentVerionOfBudgetCosts(): List<CurrentVersionOfBudgetCostTestInput<out ProjectPartnerBudgetBase, out BaseBudgetEntry>> {
        val staffCostEntity = projectPartnerBudgetStaffCostEntity()
        val travelEntity = projectPartnerBudgetTravelEntity()
        val infrastructureEntity = projectPartnerBudgetInfrastructureEntity()
        val externalEntity = projectPartnerBudgetExternalEntity()
        val equipmentEntity = projectPartnerBudgetEquipmentEntity()
        val unitCostEntity = projectPartnerBudgetUnitCostEntity()
        return listOf(
            CurrentVersionOfBudgetCostTestInput(
                "staff costs", staffCostEntity, budgetStaffCostRepository,
                persistence::getBudgetStaffCosts, budgetStaffCostEntry(staffCostEntity)
            ),
            CurrentVersionOfBudgetCostTestInput(
                "travel and accommodation costs", travelEntity, budgetTravelRepository,
                persistence::getBudgetTravelAndAccommodationCosts, budgetTravelAndAccommodationCostEntry(travelEntity)
            ),
            CurrentVersionOfBudgetCostTestInput(
                "infrastructure and works costs", infrastructureEntity, budgetInfrastructureRepository,
                persistence::getBudgetInfrastructureAndWorksCosts, budgetGeneralCostEntry(infrastructureEntity)
            ),
            CurrentVersionOfBudgetCostTestInput(
                "external costs", externalEntity, budgetExternalRepository,
                persistence::getBudgetExternalExpertiseAndServicesCosts, budgetGeneralCostEntry(externalEntity)
            ),
            CurrentVersionOfBudgetCostTestInput(
                "equipment costs", equipmentEntity, budgetEquipmentRepository,
                persistence::getBudgetEquipmentCosts, budgetGeneralCostEntry(equipmentEntity)
            ),
            CurrentVersionOfBudgetCostTestInput(
                "unit costs", unitCostEntity, budgetUnitCostRepository,
                persistence::getBudgetUnitCosts, budgetUnitCostEntry(unitCostEntity)
            )
        )
    }

    protected fun testInputsForGettingPreviousVersionOfBudgetCosts(): List<PreviousVersionOfBudgetCostTestInput<out ProjectPartnerBudgetBase, out BaseBudgetEntry, out Any>> {
        val staffCostRow = mockBudgetStaffCostRow()
        val travelRow = mockBudgetTravelCostRow()
        val generalRow = mockBudgetGeneralRow()
        val unitCostRow = mockBudgetUnitCostRow()
        return listOf(
            PreviousVersionOfBudgetCostTestInput(
                "staff costs", staffCostRow, budgetStaffCostRepository,
                persistence::getBudgetStaffCosts, budgetStaffCostEntry(staffCostRow),
                ProjectPartnerBudgetStaffCostRow::class
            ),
            PreviousVersionOfBudgetCostTestInput(
                "travel and accommodation costs", travelRow, budgetTravelRepository,
                persistence::getBudgetTravelAndAccommodationCosts, budgetTravelAndAccommodationCostEntry(travelRow),
                ProjectPartnerBudgetTravelCostRow::class
            ),
            PreviousVersionOfBudgetCostTestInput(
                "infrastructure and works costs", generalRow, budgetInfrastructureRepository,
                persistence::getBudgetInfrastructureAndWorksCosts, budgetGeneralCostEntry(generalRow),
                ProjectPartnerBudgetGeneralRow::class
            ),
            PreviousVersionOfBudgetCostTestInput(
                "external costs", generalRow, budgetExternalRepository,
                persistence::getBudgetExternalExpertiseAndServicesCosts, budgetGeneralCostEntry(generalRow),
                ProjectPartnerBudgetGeneralRow::class
            ),
            PreviousVersionOfBudgetCostTestInput(
                "equipment costs", generalRow, budgetEquipmentRepository,
                persistence::getBudgetEquipmentCosts, budgetGeneralCostEntry(generalRow),
                ProjectPartnerBudgetGeneralRow::class
            ),
            PreviousVersionOfBudgetCostTestInput(
                "unit costs", unitCostRow, budgetUnitCostRepository,
                persistence::getBudgetUnitCosts, budgetUnitCostEntry(unitCostRow),
                ProjectPartnerBudgetUnitCostRow::class, true
            )
        )
    }

    protected fun testInputsForGettingCostsTotal() =
        listOf(
            Triple("staff costs total", budgetStaffCostRepository, persistence::getBudgetStaffCostTotal),
            Triple(
                "travel and accommodation costs total", budgetTravelRepository,
                persistence::getBudgetTravelAndAccommodationCostTotal
            ),
            Triple(
                "infrastructure and works costs total", budgetInfrastructureRepository,
                persistence::getBudgetInfrastructureAndWorksCostTotal
            ),
            Triple(
                "external costs total", budgetExternalRepository,
                persistence::getBudgetExternalExpertiseAndServicesCostTotal
            ),
            Triple("equipment costs total", budgetEquipmentRepository, persistence::getBudgetEquipmentCostTotal),
            Triple("unit costs total", budgetUnitCostRepository, persistence::getBudgetUnitCostTotal),
        )

    private fun projectPartnerBudgetStaffCostEntity() =
        ProjectPartnerBudgetStaffCostEntity(
            id = entityId,
            baseProperties = BaseBudgetProperties(
                partnerId = partnerId,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN
            ),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = unitCostId
        )

    private fun budgetStaffCostEntry(entity: ProjectPartnerBudgetStaffCostEntity) =
        BudgetStaffCostEntry(
            id = entity.id,
            budgetPeriods = mutableSetOf(),
            unitCostId = entity.unitCostId,
            numberOfUnits = entity.baseProperties.numberOfUnits,
            pricePerUnit = entity.pricePerUnit,
            rowSum = entity.baseProperties.rowSum
        )

    private fun budgetStaffCostEntry(staffCostRow: ProjectPartnerBudgetStaffCostRow) =
        BudgetStaffCostEntry(
            id = staffCostRow.getId(),
            unitCostId = staffCostRow.getUnitCostId(),
            numberOfUnits = staffCostRow.getNumberOfUnits(),
            pricePerUnit = staffCostRow.getPricePerUnit(),
            rowSum = staffCostRow.getRowSum(),
            description = setOf(InputTranslation(staffCostRow.language!!, staffCostRow.getDescription())),
            comment = setOf(InputTranslation(staffCostRow.language!!, staffCostRow.getComment())),
            unitType = setOf(InputTranslation(staffCostRow.language!!, staffCostRow.getUnitType())),
            budgetPeriods = mutableSetOf(
                BudgetPeriod(
                    number = staffCostRow.getPeriodNumber()!!,
                    amount = staffCostRow.getAmount()
                )
            )
        )

    private fun mockBudgetStaffCostRow(): ProjectPartnerBudgetStaffCostRow {
        val mockRow: ProjectPartnerBudgetStaffCostRow = mockk()
        every { mockRow.getId() } returns entityId
        every { mockRow.language } returns SystemLanguage.EN
        every { mockRow.getDescription() } returns description
        every { mockRow.getPartnerId() } returns partnerId
        every { mockRow.getNumberOfUnits() } returns BigDecimal.ONE
        every { mockRow.getUnitCostId() } returns unitCostId
        every { mockRow.getPricePerUnit() } returns BigDecimal.TEN
        every { mockRow.getRowSum() } returns BigDecimal.TEN
        every { mockRow.getComment() } returns "comment"
        every { mockRow.getUnitType() } returns unitType
        every { mockRow.getPeriodNumber() } returns periodNumber
        every { mockRow.getAmount() } returns BigDecimal.TEN
        return mockRow
    }


    private fun projectPartnerBudgetTravelEntity() =
        ProjectPartnerBudgetTravelEntity(
            id = entityId,
            baseProperties = BaseBudgetProperties(
                partnerId = partnerId,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN
            ),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = unitCostId
        )

    private fun budgetTravelAndAccommodationCostEntry(entity: ProjectPartnerBudgetTravelEntity) =
        BudgetTravelAndAccommodationCostEntry(
            id = entity.id,
            budgetPeriods = mutableSetOf(),
            unitCostId = entity.unitCostId,
            numberOfUnits = entity.baseProperties.numberOfUnits,
            pricePerUnit = entity.pricePerUnit,
            rowSum = entity.baseProperties.rowSum
        )

    private fun budgetTravelAndAccommodationCostEntry(travelRow: ProjectPartnerBudgetTravelCostRow) =
        BudgetTravelAndAccommodationCostEntry(
            id = travelRow.getId(),
            unitCostId = travelRow.getUnitCostId(),
            numberOfUnits = travelRow.getNumberOfUnits(),
            pricePerUnit = travelRow.getPricePerUnit(),
            rowSum = travelRow.getRowSum(),
            description = setOf(InputTranslation(travelRow.language!!, travelRow.getDescription())),
            unitType = setOf(InputTranslation(travelRow.language!!, travelRow.getUnitType())),
            budgetPeriods = mutableSetOf(
                BudgetPeriod(
                    number = travelRow.getPeriodNumber()!!,
                    amount = travelRow.getAmount()
                )
            )
        )

    private fun mockBudgetTravelCostRow(): ProjectPartnerBudgetTravelCostRow {
        val mockRow: ProjectPartnerBudgetTravelCostRow = mockk()
        every { mockRow.getId() } returns entityId
        every { mockRow.language } returns SystemLanguage.EN
        every { mockRow.getDescription() } returns description
        every { mockRow.getPartnerId() } returns partnerId
        every { mockRow.getNumberOfUnits() } returns BigDecimal.ONE
        every { mockRow.getUnitCostId() } returns unitCostId
        every { mockRow.getPricePerUnit() } returns BigDecimal.TEN
        every { mockRow.getRowSum() } returns BigDecimal.TEN
        every { mockRow.getUnitType() } returns unitType
        every { mockRow.getPeriodNumber() } returns periodNumber
        every { mockRow.getAmount() } returns BigDecimal.TEN
        return mockRow
    }

    private fun projectPartnerBudgetInfrastructureEntity() =
        ProjectPartnerBudgetInfrastructureEntity(
            id = entityId,
            baseProperties = BaseBudgetProperties(
                partnerId = 1,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN
            ),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = unitCostId,
            investmentId = investmentId
        )

    private fun projectPartnerBudgetExternalEntity() =
        ProjectPartnerBudgetExternalEntity(
            id = entityId,
            baseProperties = BaseBudgetProperties(
                partnerId = partnerId,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN
            ),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = unitCostId,
            investmentId = investmentId
        )

    private fun projectPartnerBudgetEquipmentEntity() =
        ProjectPartnerBudgetEquipmentEntity(
            id = entityId,
            baseProperties = BaseBudgetProperties(
                partnerId = partnerId,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN
            ),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = unitCostId,
            investmentId = investmentId
        )

    private fun budgetGeneralCostEntry(entity: ProjectPartnerBudgetGeneralBase) =
        BudgetGeneralCostEntry(
            id = entity.id,
            budgetPeriods = mutableSetOf(),
            unitCostId = entity.unitCostId,
            numberOfUnits = entity.baseProperties.numberOfUnits,
            pricePerUnit = entity.pricePerUnit,
            rowSum = entity.baseProperties.rowSum,
            investmentId = entity.investmentId
        )

    private fun mockBudgetGeneralRow(): ProjectPartnerBudgetGeneralRow {
        val mockRow: ProjectPartnerBudgetGeneralRow = mockk()
        every { mockRow.getId() } returns entityId
        every { mockRow.language } returns SystemLanguage.EN
        every { mockRow.getDescription() } returns description
        every { mockRow.getPartnerId() } returns partnerId
        every { mockRow.getNumberOfUnits() } returns BigDecimal.ONE
        every { mockRow.getUnitCostId() } returns unitCostId
        every { mockRow.getPricePerUnit() } returns BigDecimal.TEN
        every { mockRow.getRowSum() } returns BigDecimal.TEN
        every { mockRow.getUnitType() } returns unitType
        every { mockRow.getPeriodNumber() } returns periodNumber
        every { mockRow.getAmount() } returns BigDecimal.TEN
        every { mockRow.getAwardProcedures() } returns awardProcedures
        every { mockRow.getInvestmentId() } returns investmentId
        return mockRow
    }

    private fun budgetGeneralCostEntry(generalRow: ProjectPartnerBudgetGeneralRow) =
        BudgetGeneralCostEntry(
            id = generalRow.getId(),
            unitCostId = generalRow.getUnitCostId(),
            numberOfUnits = generalRow.getNumberOfUnits(),
            pricePerUnit = generalRow.getPricePerUnit(),
            rowSum = generalRow.getRowSum(),
            investmentId = generalRow.getInvestmentId(),
            description = setOf(InputTranslation(generalRow.language!!, generalRow.getDescription())),
            unitType = setOf(InputTranslation(generalRow.language!!, generalRow.getUnitType())),
            awardProcedures = setOf(InputTranslation(generalRow.language!!, generalRow.getAwardProcedures())),
            budgetPeriods = mutableSetOf(
                BudgetPeriod(
                    number = generalRow.getPeriodNumber()!!,
                    amount = generalRow.getAmount()
                )
            )
        )

    private fun projectPartnerBudgetUnitCostEntity() =
        ProjectPartnerBudgetUnitCostEntity(
            id = entityId,
            baseProperties = BaseBudgetProperties(
                partnerId = partnerId,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN
            ),
            budgetPeriodEntities = mutableSetOf(),
            unitCost = ProgrammeUnitCostEntity(id = entityId, costPerUnit = BigDecimal.TEN, isOneCostCategory = true)
        )

    private fun budgetUnitCostEntry(entity: ProjectPartnerBudgetUnitCostEntity) =
        BudgetUnitCostEntry(
            id = entity.id,
            budgetPeriods = mutableSetOf(),
            unitCostId = entity.unitCost.id,
            numberOfUnits = entity.baseProperties.numberOfUnits,
            rowSum = entity.baseProperties.rowSum,
        )

    private fun budgetUnitCostEntry(row: ProjectPartnerBudgetUnitCostRow) =
        BudgetUnitCostEntry(
            id = row.getId(),
            unitCostId = row.getUnitCostId(),
            numberOfUnits = row.getNumberOfUnits(),
            rowSum = row.getRowSum(),
            budgetPeriods = mutableSetOf(
                BudgetPeriod(
                    number = row.getPeriodNumber()!!,
                    amount = row.getAmount()
                )
            )
        )

    private fun mockBudgetUnitCostRow(): ProjectPartnerBudgetUnitCostRow {
        val mockRow: ProjectPartnerBudgetUnitCostRow = mockk()
        every { mockRow.getId() } returns entityId
        every { mockRow.getPartnerId() } returns partnerId
        every { mockRow.getNumberOfUnits() } returns BigDecimal.ONE
        every { mockRow.getUnitCostId() } returns unitCostId
        every { mockRow.getRowSum() } returns BigDecimal.TEN
        every { mockRow.getPeriodNumber() } returns periodNumber
        every { mockRow.getAmount() } returns BigDecimal.TEN
        return mockRow
    }
}
