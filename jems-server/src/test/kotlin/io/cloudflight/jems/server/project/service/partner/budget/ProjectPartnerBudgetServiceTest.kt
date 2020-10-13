package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipment
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternal
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructure
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetCommonRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProjectPartnerBudgetServiceTest {

    companion object {

        private val userRole = UserRole(1, "ADMIN")
        private val user = User(
            id = 1,
            name = "Name",
            password = "hash",
            email = "admin@admin.dev",
            surname = "Surname",
            userRole = userRole)

        private val dummyCall = Call(
            id = 1,
            creator = user,
            name = "call",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            priorityPolicies = emptySet(),
            strategies = emptySet(),
            funds = emptySet(),
            lengthOfPeriod = 1
        )
        private val projectStatus = ProjectStatus(
            status = ProjectApplicationStatus.APPROVED,
            user = user,
            updated = ZonedDateTime.now())
        private val project = Project(
            id = 1,
            acronym = "acronym",
            call = dummyCall,
            applicant = user,
            projectStatus = projectStatus)

        private val projectPartner = ProjectPartner(
            id = 1,
            project = project,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER)

        private fun staffCost(id: Long, numberOfUnits: Double, pricePerUnit: Double): ProjectPartnerBudgetStaffCost {
            return ProjectPartnerBudgetStaffCost(
                id = id,
                partnerId = projectPartner.id!!,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit)
                )
            )
        }

        private fun travel(id: Long, numberOfUnits: Double, pricePerUnit: Double): ProjectPartnerBudgetTravel {
            return ProjectPartnerBudgetTravel(
                id = id,
                partnerId = projectPartner.id!!,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit)
                )
            )
        }

        private fun external(id: Long, numberOfUnits: Double, pricePerUnit: Double): ProjectPartnerBudgetExternal {
            return ProjectPartnerBudgetExternal(
                id = id,
                partnerId = projectPartner.id!!,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit)
                )
            )
        }

        private fun equipment(id: Long, numberOfUnits: Double, pricePerUnit: Double): ProjectPartnerBudgetEquipment {
            return ProjectPartnerBudgetEquipment(
                id = id,
                partnerId = projectPartner.id!!,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit)
                )
            )
        }

        private fun infrastructure(id: Long, numberOfUnits: Double, pricePerUnit: Double): ProjectPartnerBudgetInfrastructure {
            return ProjectPartnerBudgetInfrastructure(
                id = id,
                partnerId = projectPartner.id!!,
                budget = Budget(
                    numberOfUnits = BigDecimal.valueOf(numberOfUnits),
                    pricePerUnit = BigDecimal.valueOf(pricePerUnit)
                )
            )
        }

        const val TO_CHANGE_NUM_OF_UNITS_OLD = 2000.00
        const val TO_CHANGE_PRICE_PER_UNIT_OLD = 662.25
        const val TO_CHANGE_NUM_OF_UNITS_NEW = 1500.00
        const val TO_CHANGE_PRICE_PER_UNIT_NEW = 773.36
        const val TO_STAY_NUM_OF_UNITS = 18.00
        const val TO_STAY_PRICE_PER_UNIT = 220_000_000.0

    }

    @MockK
    lateinit var veryBigList: List<InputBudget>

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

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

    lateinit var projectPartnerBudgetService: ProjectPartnerBudgetService

    @BeforeAll
    fun setup() {
        MockKAnnotations.init(this)
        projectPartnerBudgetService = ProjectPartnerBudgetServiceImpl(
            projectPartnerRepository,
            projectPartnerBudgetStaffCostRepository,
            projectPartnerBudgetTravelRepository,
            projectPartnerBudgetExternalRepository,
            projectPartnerBudgetEquipmentRepository,
            projectPartnerBudgetInfrastructureRepository
        )
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `save Budget test maximum`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>
    ) {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns emptyList()
        every { repository.deleteAll(any<List<ProjectPartnerBudgetStaffCost>>()) } answers {}
        every { repository.saveAll(any<List<ProjectPartnerBudgetStaffCost>>()) } returnsArgument 0

        val toBeSavedMaxNumberOfUnits = listOf(
            InputBudget(
                numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                pricePerUnit = BigDecimal.ONE)
        )

        assertThat(
            callUpdateBudgetServiceMethod(repository,1, 1, toBeSavedMaxNumberOfUnits)
        ).overridingErrorMessage("numberOfUnits could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputBudget(
                    numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                    pricePerUnit = BigDecimal.valueOf(100L, 2))
            )
        )

        val toBeSavedMaxPricePerUnit = listOf(
            InputBudget(
                numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                pricePerUnit = BigDecimal.ONE)
        )

        assertThat(
            callUpdateBudgetServiceMethod(repository, 1, 1, toBeSavedMaxPricePerUnit)
        ).overridingErrorMessage("pricePerUnit could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputBudget(
                    numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                    pricePerUnit = BigDecimal.valueOf(100L, 2))
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `save Budget more items than allowed`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>
    ) {
        every { veryBigList.size } returns 301
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        assertThat(
            assertThrows<I18nValidationException> { callUpdateBudgetServiceMethod(repository, 1, 1, veryBigList) }.i18nKey
        ).isEqualTo("project.partner.budget.max.allowed.reached")
    }

    @ParameterizedTest
    @MethodSource("provideAllBudgetRepositories")
    fun `save Budget StuffCosts number out of range`(
        repository: ProjectPartnerBudgetCommonRepository<CommonBudget>
    ) {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(repository, 1, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_991L, 3),
                        pricePerUnit = BigDecimal.ONE
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(repository, 1, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.ONE,
                        pricePerUnit = BigDecimal.valueOf(999_999_999_999_999_991L, 3)
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                callUpdateBudgetServiceMethod(repository, 1, 1, listOf(
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
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns existing

        assertThat(callGetBudgetServiceMethod(repository, 1, 1))
            .isEqualTo(
                listOf(
                    InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(TO_CHANGE_NUM_OF_UNITS_OLD), pricePerUnit = BigDecimal.valueOf(TO_CHANGE_PRICE_PER_UNIT_OLD)),
                    InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(TO_STAY_NUM_OF_UNITS), pricePerUnit = BigDecimal.valueOf(TO_STAY_PRICE_PER_UNIT)),
                    InputBudget(id = 3, numberOfUnits = BigDecimal.valueOf(1.00), pricePerUnit = BigDecimal.valueOf(1000000.1))
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

        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { repository.findAllByPartnerIdOrderByIdAsc(1) } returns existing
        every { repository.deleteAll(any<List<ProjectPartnerBudgetTravel>>()) } answers {}
        every { repository.saveAll(any<List<ProjectPartnerBudgetTravel>>()) } returnsArgument 0

        assertThat(
            callUpdateBudgetServiceMethod(repository, 1, 1, toBeSaved)
        ).isEqualTo(
            listOf(
                InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf((TO_CHANGE_NUM_OF_UNITS_NEW * 100).toLong(), 2), pricePerUnit = BigDecimal.valueOf((TO_CHANGE_PRICE_PER_UNIT_NEW * 100).toLong(), 2)),
                InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf((TO_STAY_NUM_OF_UNITS * 100).toLong(), 2), pricePerUnit = BigDecimal.valueOf((TO_STAY_PRICE_PER_UNIT * 100).toLong(), 2)),
                // here there should be id=4 normally
                InputBudget(id = null, numberOfUnits = BigDecimal.valueOf(55000, 2), pricePerUnit = BigDecimal.valueOf(1000, 2))
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
        projectId: Long,
        partnerId: Long,
        toBeSaved: List<InputBudget>
    ): List<InputBudget> {
        if (repo is ProjectPartnerBudgetStaffCostRepository)
            return projectPartnerBudgetService.updateStaffCosts(projectId, partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetTravelRepository)
            return projectPartnerBudgetService.updateTravel(projectId, partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetExternalRepository)
            return projectPartnerBudgetService.updateExternal(projectId, partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetEquipmentRepository)
            return projectPartnerBudgetService.updateEquipment(projectId, partnerId, toBeSaved)
        if (repo is ProjectPartnerBudgetInfrastructureRepository)
            return projectPartnerBudgetService.updateInfrastructure(projectId, partnerId, toBeSaved)
        throw UnsupportedOperationException()
    }

    private fun callGetBudgetServiceMethod(
        repo: ProjectPartnerBudgetCommonRepository<CommonBudget>,
        projectId: Long,
        partnerId: Long
    ): List<InputBudget> {
        if (repo is ProjectPartnerBudgetStaffCostRepository)
            return projectPartnerBudgetService.getStaffCosts(projectId, partnerId)
        if (repo is ProjectPartnerBudgetTravelRepository)
            return projectPartnerBudgetService.getTravel(projectId, partnerId)
        if (repo is ProjectPartnerBudgetExternalRepository)
            return projectPartnerBudgetService.getExternal(projectId, partnerId)
        if (repo is ProjectPartnerBudgetEquipmentRepository)
            return projectPartnerBudgetService.getEquipment(projectId, partnerId)
        if (repo is ProjectPartnerBudgetInfrastructureRepository)
            return projectPartnerBudgetService.getInfrastructure(projectId, partnerId)
        throw UnsupportedOperationException()
    }

    private fun provideAllBudgetRepositories(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                projectPartnerBudgetStaffCostRepository,
                // existing budget lines
                listOf(
                    staffCost(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD),
                    staffCost(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT),
                    staffCost(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    staffCost(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetTravelRepository,
                // existing budget lines
                listOf(
                    travel(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD),
                    travel(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT),
                    travel(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    travel(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetExternalRepository,
                // existing budget lines
                listOf(
                    external(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD),
                    external(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT),
                    external(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    external(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetEquipmentRepository,
                // existing budget lines
                listOf(
                    equipment(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD),
                    equipment(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT),
                    equipment(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    equipment(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                )),
            Arguments.of(
                projectPartnerBudgetInfrastructureRepository,
                // existing budget lines
                listOf(
                    infrastructure(id = 1, numberOfUnits = TO_CHANGE_NUM_OF_UNITS_OLD, pricePerUnit = TO_CHANGE_PRICE_PER_UNIT_OLD),
                    infrastructure(id = 2, numberOfUnits = TO_STAY_NUM_OF_UNITS, pricePerUnit = TO_STAY_PRICE_PER_UNIT),
                    infrastructure(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                ),
                // to be removed budget lines
                setOf(
                    infrastructure(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)
                ))
        )
    }

}
