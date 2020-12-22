package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputTravelBudget
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravelEntity
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetCommonRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
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
import java.util.stream.Stream

/**
 * Tests currently ProjectPartnerBudget services for Travel.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProjectPartnerTravelBudgetServiceTest {

    companion object {

        const val PARTNER_ID = 1L

        private fun travel(id: Long, numberOfUnits: Double, pricePerUnit: Double, rowSum: Double): ProjectPartnerBudgetTravelEntity {
            return ProjectPartnerBudgetTravelEntity(
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
        const val TO_STAY_PRICE_PER_UNIT = 22_000_000.0
        const val TO_STAY_ROW_SUM = 396_000_000.00

        private fun toBd(value: Double): BigDecimal {
            return BigDecimal.valueOf((value * 100).toLong(), 2)
        }
    }

    @MockK
    lateinit var veryBigList: List<InputTravelBudget>

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
        every { repository.deleteAll(any<List<ProjectPartnerBudgetTravelEntity>>()) } answers {}
        every { repository.saveAll(any<List<ProjectPartnerBudgetTravelEntity>>()) } returnsArgument 0

        val toBeSavedMaxNumberOfUnits = listOf(
            InputTravelBudget(
                numberOfUnits = BigDecimal.valueOf(999_999_999_99L, 2),
                pricePerUnit = BigDecimal.ONE)
        )

        assertThat(
            callUpdateBudgetServiceMethod(1, toBeSavedMaxNumberOfUnits)
        ).overridingErrorMessage("numberOfUnits could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputTravelBudget(
                    id = 0,
                    numberOfUnits = BigDecimal.valueOf(999_999_999_99L, 2),
                    pricePerUnit = BigDecimal.valueOf(100L, 2),
                    rowSum = BigDecimal.valueOf(999_999_999_99L, 2))
            )
        )

        val toBeSavedMaxPricePerUnit = listOf(
            InputTravelBudget(
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.valueOf(999_999_999_99L, 2))
        )

        assertThat(
            callUpdateBudgetServiceMethod(1, toBeSavedMaxPricePerUnit)
        ).overridingErrorMessage("pricePerUnit could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputTravelBudget(
                    id = 0,
                    numberOfUnits = BigDecimal.valueOf(100L, 2),
                    pricePerUnit = BigDecimal.valueOf(999_999_999_99L, 2),
                    rowSum = BigDecimal.valueOf(999_999_999_99L, 2))
            )
        )
    }

    @Test
    fun `save Budget more items than allowed`() {
        every { veryBigList.size } returns 301

        assertThat(
            assertThrows<I18nValidationException> { callUpdateBudgetServiceMethod(1, veryBigList) }.i18nKey
        ).isEqualTo("project.partner.budget.max.allowed.reached")
    }

    @Test
    fun `save Budget StuffCosts number out of range`() {
        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(1, listOf(
                    InputTravelBudget(
                        numberOfUnits = BigDecimal.valueOf(999_999_999_991L, 3),
                        pricePerUnit = BigDecimal.ONE
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(1, listOf(
                    InputTravelBudget(
                        numberOfUnits = BigDecimal.ONE,
                        pricePerUnit = BigDecimal.valueOf(999_999_999_991L, 3)
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(1, listOf(
                    InputTravelBudget(
                        numberOfUnits = BigDecimal.TEN,
                        pricePerUnit = BigDecimal.valueOf(100_000_000_00L, 2)
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
                    InputTravelBudget(id = 1, numberOfUnits = BigDecimal.valueOf(TO_CHANGE_NUM_OF_UNITS_OLD), pricePerUnit = BigDecimal.valueOf(TO_CHANGE_PRICE_PER_UNIT_OLD), rowSum = BigDecimal.valueOf(TO_CHANGE_ROW_SUM_OLD)),
                    InputTravelBudget(id = 2, numberOfUnits = BigDecimal.valueOf(TO_STAY_NUM_OF_UNITS), pricePerUnit = BigDecimal.valueOf(TO_STAY_PRICE_PER_UNIT), rowSum = BigDecimal.valueOf(TO_STAY_ROW_SUM)),
                    InputTravelBudget(id = 3, numberOfUnits = BigDecimal.valueOf(1.00), pricePerUnit = BigDecimal.valueOf(1000000.1), rowSum = BigDecimal.valueOf(1000000.1))
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
            InputTravelBudget(id = 1, numberOfUnits = BigDecimal.valueOf(TO_CHANGE_NUM_OF_UNITS_NEW), pricePerUnit = BigDecimal.valueOf(TO_CHANGE_PRICE_PER_UNIT_NEW)),
            // no change for existing
            InputTravelBudget(id = 2, numberOfUnits = BigDecimal.valueOf(TO_STAY_NUM_OF_UNITS), pricePerUnit = BigDecimal.valueOf(TO_STAY_PRICE_PER_UNIT)),
            // new to be created
            InputTravelBudget(id = null, numberOfUnits = BigDecimal.valueOf(550), pricePerUnit = BigDecimal.valueOf(10.0))
        )

        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns existing
        every { repository.deleteAll(any<List<ProjectPartnerBudgetTravelEntity>>()) } answers {}
        every { repository.saveAll(any<List<ProjectPartnerBudgetTravelEntity>>()) } returnsArgument 0

        assertThat(
            callUpdateBudgetServiceMethod(1, toBeSaved)
        ).isEqualTo(
            listOf(
                InputTravelBudget(id = 1, numberOfUnits = toBd(TO_CHANGE_NUM_OF_UNITS_NEW), pricePerUnit = toBd(TO_CHANGE_PRICE_PER_UNIT_NEW), rowSum = toBd(TO_CHANGE_ROW_SUM_NEW)),
                InputTravelBudget(id = 2, numberOfUnits = toBd(TO_STAY_NUM_OF_UNITS), pricePerUnit = toBd(TO_STAY_PRICE_PER_UNIT), rowSum = toBd(TO_STAY_ROW_SUM)),
                // here there should be id=4 normally
                InputTravelBudget(id = 0, numberOfUnits = toBd(550.0), pricePerUnit = toBd(10.0), rowSum = toBd(5500.0))
            )
        )

        val whatHasBeenDeleted = slot<Set<CommonBudget>>()
        verify { repository.deleteAll(capture(whatHasBeenDeleted)) }
        assertThat(whatHasBeenDeleted.captured).isEqualTo(toBeDeleted)

        // clean context
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns emptyList()
        every { repository.saveAll(any<List<ProjectPartnerBudgetTravelEntity>>()) } throws UnsupportedOperationException()
    }

    private fun callUpdateBudgetServiceMethod(
        partnerId: Long,
        toBeSaved: List<InputTravelBudget>
    ): List<InputTravelBudget> {
        return projectPartnerBudgetService.updateTravel(partnerId, toBeSaved)
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
                ))
        )
    }

}
