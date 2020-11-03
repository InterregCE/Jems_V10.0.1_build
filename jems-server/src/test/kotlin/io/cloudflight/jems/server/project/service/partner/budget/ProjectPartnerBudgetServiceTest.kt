package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipment
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternal
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructure
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetCommonRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.Optional
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProjectPartnerBudgetServiceTest {

    companion object {

        const val PARTNER_ID = 1L

        private val projectPartner = ProjectPartner(
            id = 1,
            project = project,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER)

        val budgetOptions = ProjectPartnerBudgetOptions(
            PARTNER_ID,
            15,
            20
        )

        private fun staffCost(id: Long, numberOfUnits: Double, pricePerUnit: Double, rowSum: Double): ProjectPartnerBudgetStaffCost {
            return ProjectPartnerBudgetStaffCost(
                id = id,
                partnerId = PARTNER_ID,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit),
                    rowSum = BigDecimal.valueOf(rowSum)
                )
            )
        }

        private fun travel(id: Long, numberOfUnits: Double, pricePerUnit: Double, rowSum: Double): ProjectPartnerBudgetTravel {
            return ProjectPartnerBudgetTravel(
                id = id,
                partnerId = PARTNER_ID,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit),
                    rowSum = BigDecimal.valueOf(rowSum)
                )
            )
        }

        private fun external(id: Long, numberOfUnits: Double, pricePerUnit: Double, rowSum: Double): ProjectPartnerBudgetExternal {
            return ProjectPartnerBudgetExternal(
                id = id,
                partnerId = PARTNER_ID,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit),
                    rowSum = BigDecimal.valueOf(rowSum)
                )
            )
        }

        private fun equipment(id: Long, numberOfUnits: Double, pricePerUnit: Double, rowSum: Double): ProjectPartnerBudgetEquipment {
            return ProjectPartnerBudgetEquipment(
                id = id,
                partnerId = PARTNER_ID,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit),
                    rowSum = BigDecimal.valueOf(rowSum)
                )
            )
        }

        private fun infrastructure(id: Long, numberOfUnits: Double, pricePerUnit: Double, rowSum: Double): ProjectPartnerBudgetInfrastructure {
            return ProjectPartnerBudgetInfrastructure(
                id = id,
                partnerId = PARTNER_ID,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit),
                    rowSum = BigDecimal.valueOf(rowSum)
                )
            )
        }

        const val TO_CHANGE_NUM_OF_UNITS_OLD = 2000.00
        const val TO_CHANGE_PRICE_PER_UNIT_OLD = 662.25
        const val TO_CHANGE_ROW_SUM_OLD = 1_324_500.00
        const val TO_CHANGE_NUM_OF_UNITS_NEW = 1500.00
        const val TO_CHANGE_PRICE_PER_UNIT_NEW = 773.36
        const val TO_CHANGE_ROW_SUM_NEW = 1_160_040.00
        const val TO_STAY_NUM_OF_UNITS = 18.00
        const val TO_STAY_PRICE_PER_UNIT = 220_000_000.0
        const val TO_STAY_ROW_SUM = 3_960_000_000.00

        private fun toBd(value: Double): BigDecimal {
            return BigDecimal.valueOf((value * 100).toLong(), 2)
        }
    }

    @MockK
    lateinit var veryBigList: List<InputBudget>

    @MockK
    lateinit var projectPartnerBudgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository

    @MockK
    lateinit var projectPartnerBudgetTravelRepository: ProjectPartnerBudgetTravelRepository

    @MockK
    lateinit var projectPartnerBudgetExternalRepository: ProjectPartnerBudgetExternalRepository

    @MockK
    lateinit var projectPartnerBudgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository

    @MockK
    lateinit var projectPartnerBudgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository

    @MockK
    lateinit var getBudgetOptionsInteractor: GetBudgetOptionsInteractor

    lateinit private var projectPartnerBudgetService: ProjectPartnerBudgetService

    @BeforeAll
    fun setup() {
        MockKAnnotations.init(this)
        projectPartnerBudgetService = ProjectPartnerBudgetServiceImpl(
            projectPartnerBudgetStaffCostRepository,
            projectPartnerBudgetTravelRepository,
            projectPartnerBudgetExternalRepository,
            projectPartnerBudgetEquipmentRepository,
            projectPartnerBudgetInfrastructureRepository,
            getBudgetOptionsInteractor
        )
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `save Budget test maximum`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>
    ) {
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns emptyList()
        every { repository.deleteAll(any<List<ProjectPartnerBudgetStaffCost>>()) } answers {}
        every { repository.saveAll(any<List<ProjectPartnerBudgetStaffCost>>()) } returnsArgument 0

        val toBeSavedMaxNumberOfUnits = listOf(
            InputBudget(
                numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                pricePerUnit = BigDecimal.ONE)
        )

        assertThat(
            callUpdateBudgetServiceMethod(repository,1, toBeSavedMaxNumberOfUnits)
        ).overridingErrorMessage("numberOfUnits could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputBudget(
                    numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                    pricePerUnit = BigDecimal.valueOf(100L, 2),
                    rowSum = BigDecimal.valueOf(999_999_999_999_999_99L, 2))
            )
        )

        val toBeSavedMaxPricePerUnit = listOf(
            InputBudget(
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.valueOf(999_999_999_999_999_99L, 2))
        )

        assertThat(
            callUpdateBudgetServiceMethod(repository, 1, toBeSavedMaxPricePerUnit)
        ).overridingErrorMessage("pricePerUnit could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputBudget(
                    numberOfUnits = BigDecimal.valueOf(100L, 2),
                    pricePerUnit = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                    rowSum = BigDecimal.valueOf(999_999_999_999_999_99L, 2))
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `save Budget more items than allowed`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>
    ) {
        every { veryBigList.size } returns 301

        assertThat(
            assertThrows<I18nValidationException> { callUpdateBudgetServiceMethod(repository, 1, veryBigList) }.i18nKey
        ).isEqualTo("project.partner.budget.max.allowed.reached")
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `save Budget StuffCosts number out of range`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>
    ) {
        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(repository, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_991L, 3),
                        pricePerUnit = BigDecimal.ONE
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(repository, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.ONE,
                        pricePerUnit = BigDecimal.valueOf(999_999_999_999_999_991L, 3)
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(repository, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.TEN,
                        pricePerUnit = BigDecimal.valueOf(100_000_000_000_000_00L, 2)
                    )
                ))
            }.i18nKey
        ).overridingErrorMessage("pricePerUnit * numberOfUnits together cannot be more then 999.999.999.999.999,99")
            .isEqualTo("project.partner.budget.number.out.of.range")
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun getProjectPartnerBudget(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>,
        existing: List<CommonBudget>
    ) {
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns existing

        assertThat(callGetBudgetServiceMethod(repository, 1))
            .isEqualTo(
                listOf(
                    InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(TO_CHANGE_NUM_OF_UNITS_OLD), pricePerUnit = BigDecimal.valueOf(TO_CHANGE_PRICE_PER_UNIT_OLD), rowSum = BigDecimal.valueOf(TO_CHANGE_ROW_SUM_OLD)),
                    InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(TO_STAY_NUM_OF_UNITS), pricePerUnit = BigDecimal.valueOf(TO_STAY_PRICE_PER_UNIT), rowSum = BigDecimal.valueOf(TO_STAY_ROW_SUM)),
                    InputBudget(id = 3, numberOfUnits = BigDecimal.valueOf(1.00), pricePerUnit = BigDecimal.valueOf(1000000.1), rowSum = BigDecimal.valueOf(1000000.1))
                )
            )

        // clean context
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns emptyList()
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `saveProjectPartnerBudget`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>,
        existing: List<CommonBudget>,
        toBeDeleted: Set<CommonBudget>
    ) {
        val toBeSaved = listOf(
            // updated existing
            InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(TO_CHANGE_NUM_OF_UNITS_NEW), pricePerUnit = BigDecimal.valueOf(TO_CHANGE_PRICE_PER_UNIT_NEW)),
            // no change for existing
            InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(TO_STAY_NUM_OF_UNITS), pricePerUnit = BigDecimal.valueOf(TO_STAY_PRICE_PER_UNIT)),
            // new to be created
            InputBudget(id = null, numberOfUnits = BigDecimal.valueOf(550), pricePerUnit = BigDecimal.valueOf(10.0))
        )

        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns existing
        every { repository.deleteAll(any<List<ProjectPartnerBudgetTravel>>()) } answers {}
        every { repository.saveAll(any<List<ProjectPartnerBudgetTravel>>()) } returnsArgument 0

        assertThat(
            callUpdateBudgetServiceMethod(repository, 1, toBeSaved)
        ).isEqualTo(
            listOf(
                InputBudget(id = 1, numberOfUnits = toBd(TO_CHANGE_NUM_OF_UNITS_NEW), pricePerUnit = toBd(TO_CHANGE_PRICE_PER_UNIT_NEW), rowSum = toBd(TO_CHANGE_ROW_SUM_NEW)),
                InputBudget(id = 2, numberOfUnits = toBd(TO_STAY_NUM_OF_UNITS), pricePerUnit = toBd(TO_STAY_PRICE_PER_UNIT), rowSum = toBd(TO_STAY_ROW_SUM)),
                // here there should be id=4 normally
                InputBudget(id = null, numberOfUnits = toBd(550.0), pricePerUnit = toBd(10.0), rowSum = toBd(5500.0))
            )
        )

        val whatHasBeenDeleted = slot<Set<CommonBudget>>()
        verify { repository.deleteAll(capture(whatHasBeenDeleted)) }
        assertThat(whatHasBeenDeleted.captured).isEqualTo(toBeDeleted)

        // clean context
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns emptyList()
        every { repository.saveAll(any<List<ProjectPartnerBudgetTravel>>()) } throws UnsupportedOperationException()
    }

    private fun callUpdateBudgetServiceMethod(
        repo: ProjectPartnerBudgetCommonRepository<CommonBudget>,
        partnerId: Long,
        toBeSaved: List<InputBudget>
    ): List<InputBudget> {
        if (repo is ProjectPartnerBudgetStaffCostRepository)
            return projectPartnerBudgetService.updateStaffCosts(partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetTravelRepository)
            return projectPartnerBudgetService.updateTravel(partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetExternalRepository)
            return projectPartnerBudgetService.updateExternal(partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetEquipmentRepository)
            return projectPartnerBudgetService.updateEquipment(partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetInfrastructureRepository)
            return projectPartnerBudgetService.updateInfrastructure(partnerId, toBeSaved)
        throw UnsupportedOperationException()
    }

    private fun callGetBudgetServiceMethod(
        repo: ProjectPartnerBudgetCommonRepository<CommonBudget>,
        partnerId: Long
    ): List<InputBudget> {
        if (repo is ProjectPartnerBudgetStaffCostRepository)
            return projectPartnerBudgetService.getStaffCosts(partnerId)
        if (repo is ProjectPartnerBudgetTravelRepository)
            return projectPartnerBudgetService.getTravel(partnerId)
        if (repo is ProjectPartnerBudgetExternalRepository)
            return projectPartnerBudgetService.getExternal(partnerId)
        if (repo is ProjectPartnerBudgetEquipmentRepository)
            return projectPartnerBudgetService.getEquipment(partnerId)
        if (repo is ProjectPartnerBudgetInfrastructureRepository)
            return projectPartnerBudgetService.getInfrastructure(partnerId)
        throw UnsupportedOperationException()
    }

    private fun provideAllBudgetRepositories(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                projectPartnerBudgetStaffCostRepository,
                // existing budget lines
                listOf(
                    staffCost(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD, rowSum = TO_CHANGE_ROW_SUM_OLD),
                    staffCost(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT, rowSum = TO_STAY_ROW_SUM),
                    staffCost(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    staffCost(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetTravelRepository,
                // existing budget lines
                listOf(
                    travel(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD, rowSum = TO_CHANGE_ROW_SUM_OLD),
                    travel(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT, rowSum = TO_STAY_ROW_SUM),
                    travel(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    travel(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetExternalRepository,
                // existing budget lines
                listOf(
                    external(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD, rowSum = TO_CHANGE_ROW_SUM_OLD),
                    external(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT, rowSum = TO_STAY_ROW_SUM),
                    external(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    external(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetEquipmentRepository,
                // existing budget lines
                listOf(
                    equipment(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD, rowSum = TO_CHANGE_ROW_SUM_OLD),
                    equipment(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT, rowSum = TO_STAY_ROW_SUM),
                    equipment(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    equipment(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetInfrastructureRepository,
                // existing budget lines
                listOf(
                    infrastructure(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD, rowSum = TO_CHANGE_ROW_SUM_OLD),
                    infrastructure(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT, rowSum = TO_STAY_ROW_SUM),
                    infrastructure(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    infrastructure(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10, rowSum = 1000_000.10)
                ))
        )
    }

    @Test
    fun `test total null`() {
        every { getBudgetOptionsInteractor.getBudgetOptions(1) } returns null
        every { projectPartnerBudgetStaffCostRepository.sumTotalForPartner(1) } returns null
        every { projectPartnerBudgetTravelRepository.sumTotalForPartner(1) } returns null
        every { projectPartnerBudgetExternalRepository.sumTotalForPartner(1) } returns null
        every { projectPartnerBudgetEquipmentRepository.sumTotalForPartner(1) } returns null
        every { projectPartnerBudgetInfrastructureRepository.sumTotalForPartner(1) } returns null

        assertThat(projectPartnerBudgetService.getTotal(1)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `test total`() {
        val id = 598L
        every { getBudgetOptionsInteractor.getBudgetOptions(id) } returns ProjectPartnerBudgetOptions(partnerId = id, officeAdministrationFlatRate = 10, staffCostsFlatRate = 10)
        every { projectPartnerBudgetStaffCostRepository.sumTotalForPartner(id) } returns BigDecimal.valueOf(5)
        every { projectPartnerBudgetTravelRepository.sumTotalForPartner(id) } returns BigDecimal.valueOf(20)
        every { projectPartnerBudgetExternalRepository.sumTotalForPartner(id) } returns BigDecimal.valueOf(8)
        every { projectPartnerBudgetEquipmentRepository.sumTotalForPartner(id) } returns BigDecimal.ZERO
        every { projectPartnerBudgetInfrastructureRepository.sumTotalForPartner(id) } returns BigDecimal.valueOf(3)

        assertThat(projectPartnerBudgetService.getTotal(id)).isEqualTo(toBd(36.5))
    }

}
