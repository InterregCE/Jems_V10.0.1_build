package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.general.equipment.ProjectPartnerBudgetEquipmentEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructureEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
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
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 * tests implementation of ProjectPartnerBudgetCostsPersistence including mappings and projectVersionUtils.
 */
class ProjectPartnerBudgetCostsPersistenceProviderTest: UnitTest() {

    private val partnerId = 1L

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

    private lateinit var persistence: ProjectPartnerBudgetCostsPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPartnerBudgetCostsPersistenceProvider(projectVersionUtils, projectPersistence, budgetStaffCostRepository, budgetTravelRepository, budgetExternalRepository, budgetEquipmentRepository, budgetInfrastructureRepository, budgetUnitCostRepository, budgetLumpSumRepository)
    }

    @Test
    fun `get budget staff costs with current version`() {
        val entity = ProjectPartnerBudgetStaffCostEntity(
            id = 1,
            baseProperties = BaseBudgetProperties(partnerId = 1, numberOfUnits = BigDecimal.ONE, rowSum = BigDecimal.TEN),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = 2
        )
        val entities = listOf(entity)

        every { projectPersistence.getProjectIdForPartner(partnerId) } returns 2
        every { budgetStaffCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns entities
        assertThat(persistence.getBudgetStaffCosts(partnerId))
            .containsExactly(BudgetStaffCostEntry(
                id = entity.id,
                budgetPeriods = mutableSetOf(),
                unitCostId = entity.unitCostId,
                numberOfUnits = entity.baseProperties.numberOfUnits,
                pricePerUnit = entity.pricePerUnit,
                rowSum = entity.baseProperties.rowSum
            ))
    }

    @Test
    fun `get budget staff costs with previous version`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val projectId = 2L
        val version = 3
        val mockRow: ProjectPartnerBudgetStaffCostRow = mockk()
        every { mockRow.id } returns 1L
        every { mockRow.language } returns SystemLanguage.EN
        every { mockRow.description } returns "description"
        every { mockRow.numberOfUnits } returns BigDecimal.ONE
        every { mockRow.unitCostId } returns 2L
        every { mockRow.pricePerUnit } returns BigDecimal.TEN
        every { mockRow.rowSum } returns BigDecimal.TEN
        every { mockRow.comment } returns "comment"
        every { mockRow.unitType } returns "unit"
        every { mockRow.periodNumber } returns 1
        every { mockRow.amount } returns BigDecimal.TEN

        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
        every { projectVersionRepo.findTimestampByVersion(projectId, version) } returns timestamp
        every { budgetStaffCostRepository.findAllByPartnerIdAsOfTimestamp(partnerId, timestamp) } returns listOf(mockRow)
        assertThat(persistence.getBudgetStaffCosts(partnerId, version))
            .containsExactly(BudgetStaffCostEntry(
                id = mockRow.id,
                unitCostId = mockRow.unitCostId,
                numberOfUnits = mockRow.numberOfUnits,
                pricePerUnit = mockRow.pricePerUnit,
                rowSum = mockRow.rowSum,
                description = setOf(InputTranslation(mockRow.language!!, mockRow.description)),
                comment = setOf(InputTranslation(mockRow.language!!, mockRow.comment)),
                unitType = setOf(InputTranslation(mockRow.language!!, mockRow.unitType)),
                budgetPeriods = mutableSetOf(BudgetPeriod(number = mockRow.periodNumber!!, amount = mockRow.amount))
            ))
    }

    @Test
    fun `get budget staff costs total`() {
        every { budgetStaffCostRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetStaffCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)
        verify { budgetStaffCostRepository.sumTotalForPartner(partnerId) }

        every { budgetStaffCostRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetStaffCostTotal(0)).isEqualTo(BigDecimal.ZERO)
        verify { budgetStaffCostRepository.sumTotalForPartner(0) }
    }

    @Test
    fun `get budget travel and accommodation costs`() {
        val entity = ProjectPartnerBudgetTravelEntity(
            id = 1,
            baseProperties = BaseBudgetProperties(partnerId = 1, numberOfUnits = BigDecimal.ONE, rowSum = BigDecimal.TEN),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = 2
        )
        val entities = listOf(entity)

        every { budgetTravelRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns entities
        assertThat(persistence.getBudgetTravelAndAccommodationCosts(partnerId))
            .containsExactly(BudgetTravelAndAccommodationCostEntry(
                id = entity.id,
                budgetPeriods = mutableSetOf(),
                unitCostId = entity.unitCostId,
                numberOfUnits = entity.baseProperties.numberOfUnits,
                pricePerUnit = entity.pricePerUnit,
                rowSum = entity.baseProperties.rowSum
            ))
    }

    @Test
    fun `get budget travel and accommodation costs total`() {
        every { budgetTravelRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetTravelAndAccommodationCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetTravelRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetTravelAndAccommodationCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `get budget infrastructure and works costs total`() {
        every { budgetInfrastructureRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetInfrastructureRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetInfrastructureAndWorksCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `get budget infrastructure and works costs`() {
        val entity = ProjectPartnerBudgetInfrastructureEntity(
            id = 1,
            baseProperties = BaseBudgetProperties(partnerId = 1, numberOfUnits = BigDecimal.ONE, rowSum = BigDecimal.TEN),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = 2,
            investmentId = 3
        )
        val entities = listOf(entity)

        every { budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns entities
        assertThat(persistence.getBudgetInfrastructureAndWorksCosts(partnerId))
            .containsExactly(BudgetGeneralCostEntry(
                id = entity.id,
                budgetPeriods = mutableSetOf(),
                unitCostId = entity.unitCostId,
                numberOfUnits = entity.baseProperties.numberOfUnits,
                pricePerUnit = entity.pricePerUnit,
                rowSum = entity.baseProperties.rowSum,
                investmentId = entity.investmentId
            ))
    }

    @Test
    fun `get budget external costs total`() {
        every { budgetExternalRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetExternalRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `get budget external costs`() {
        val entity = ProjectPartnerBudgetExternalEntity(
            id = 1,
            baseProperties = BaseBudgetProperties(partnerId = 1, numberOfUnits = BigDecimal.ONE, rowSum = BigDecimal.TEN),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = 2,
            investmentId = 3
        )
        val entities = listOf(entity)

        every { budgetExternalRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns entities
        assertThat(persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId))
            .containsExactly(BudgetGeneralCostEntry(
                id = entity.id,
                budgetPeriods = mutableSetOf(),
                unitCostId = entity.unitCostId,
                numberOfUnits = entity.baseProperties.numberOfUnits,
                pricePerUnit = entity.pricePerUnit,
                rowSum = entity.baseProperties.rowSum,
                investmentId = entity.investmentId
            ))
    }

    @Test
    fun `get budget equipment costs total`() {
        every { budgetEquipmentRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetEquipmentCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetEquipmentRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetEquipmentCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `get budget equipment costs`() {
        val entity = ProjectPartnerBudgetEquipmentEntity(
            id = 1,
            baseProperties = BaseBudgetProperties(partnerId = 1, numberOfUnits = BigDecimal.ONE, rowSum = BigDecimal.TEN),
            pricePerUnit = BigDecimal.TEN,
            budgetPeriodEntities = mutableSetOf(),
            unitCostId = 2,
            investmentId = 3
        )
        val entities = listOf(entity)

        every { budgetEquipmentRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns entities
        assertThat(persistence.getBudgetEquipmentCosts(partnerId))
            .containsExactly(BudgetGeneralCostEntry(
                id = entity.id,
                budgetPeriods = mutableSetOf(),
                unitCostId = entity.unitCostId,
                numberOfUnits = entity.baseProperties.numberOfUnits,
                pricePerUnit = entity.pricePerUnit,
                rowSum = entity.baseProperties.rowSum,
                investmentId = entity.investmentId
            ))
    }

    @Test
    fun `get budget unit costs total`() {
        every { budgetUnitCostRepository.sumTotalForPartner(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetUnitCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetUnitCostRepository.sumTotalForPartner(0) } returns null
        assertThat(persistence.getBudgetUnitCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `get budget unit costs`() {
        val entity = ProjectPartnerBudgetUnitCostEntity(
            id = 1,
            baseProperties = BaseBudgetProperties(partnerId = 1, numberOfUnits = BigDecimal.ONE, rowSum = BigDecimal.TEN),
            budgetPeriodEntities = mutableSetOf(),
            unitCost = ProgrammeUnitCostEntity(id = 1, costPerUnit = BigDecimal.TEN, isOneCostCategory = true)
        )
        val entities = listOf(entity)

        every { budgetUnitCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId) } returns entities
        assertThat(persistence.getBudgetUnitCosts(partnerId))
            .containsExactly(BudgetUnitCostEntry(
                id = entity.id,
                budgetPeriods = mutableSetOf(),
                unitCostId = entity.unitCost.id,
                numberOfUnits = entity.baseProperties.numberOfUnits,
                rowSum = entity.baseProperties.rowSum,
            ))
    }

    @Test
    fun `get budget lump sums total`() {
        every { budgetLumpSumRepository.getPartnerLumpSumsTotal(partnerId) } returns BigDecimal.TEN
        assertThat(persistence.getBudgetLumpSumsCostTotal(partnerId)).isEqualTo(BigDecimal.TEN)

        every { budgetLumpSumRepository.getPartnerLumpSumsTotal(0) } returns null
        assertThat(persistence.getBudgetLumpSumsCostTotal(0)).isEqualTo(BigDecimal.ZERO)
    }

}
