package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectPartner
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import io.cloudflight.jems.server.project.repository.ProjectPartnerRepository
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
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional

class ProjectPartnerBudgetServiceTest {

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
            name = "partner",
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

    @BeforeEach
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

    //region StaffCosts

    @Test
    fun getProjectPartnerBudgetStaffCost() {
        val projectPartnerBudgetStaffCosts = listOf(
            staffCost(id = 1, numberOfUnits = 2.00, pricePerUnit = 15600.00),
            staffCost(id = 2, numberOfUnits = 1.00, pricePerUnit = 220000.12),
            staffCost(id = 3, numberOfUnits = 15.18, pricePerUnit = 180.13)
        )

        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerBudgetStaffCostRepository.findAllByPartnerIdOrderByIdAsc(1) } returns projectPartnerBudgetStaffCosts

        assertThat(projectPartnerBudgetService.getStaffCosts(1, 1))
            .isEqualTo(
                listOf(
                    InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(2.00), pricePerUnit = BigDecimal.valueOf(15600.00)),
                    InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(1.00), pricePerUnit = BigDecimal.valueOf(220000.12)),
                    InputBudget(id = 3, numberOfUnits = BigDecimal.valueOf(15.18), pricePerUnit = BigDecimal.valueOf(180.13))
                )
            )
    }

    @Test
    fun `getProjectPartnerBudgetStaffCost not visible`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(-1, -1) } returns Optional.empty()

        assertThat(
            assertThrows<ResourceNotFoundException> { projectPartnerBudgetService.getStaffCosts(-1, -1) }.entity
        ).isEqualTo("projectPartner")
    }

    @Test
    fun `save BudgetStaffCost`() {
        val existing = listOf(
            staffCost(id = 1, numberOfUnits = 2.00, pricePerUnit = 15600.00),
            staffCost(id = 2, numberOfUnits = 1.00, pricePerUnit = 220000.12),
            staffCost(id = 3, numberOfUnits = 15.18, pricePerUnit = 180.13) // to be removed
        )
        val toBeSaved = listOf(
            // updated existing
            InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(3), pricePerUnit = BigDecimal.valueOf(15600.0)),
            // no change for existing
            InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(1), pricePerUnit = BigDecimal.valueOf(220000.125)),
            // new to be created
            InputBudget(id = null, numberOfUnits = BigDecimal.valueOf(18.217), pricePerUnit = BigDecimal.valueOf(50.009))
        )

        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerBudgetStaffCostRepository.findAllByPartnerIdOrderByIdAsc(1) } returns existing
        every { projectPartnerBudgetStaffCostRepository.deleteAll(any<List<ProjectPartnerBudgetStaffCost>>()) } answers {}
        every { projectPartnerBudgetStaffCostRepository.saveAll(any<List<ProjectPartnerBudgetStaffCost>>()) } returnsArgument 0

        assertThat(
            projectPartnerBudgetService.updateStaffCosts(1, 1, toBeSaved)
        ).isEqualTo(
            listOf(
                InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(300, 2), pricePerUnit = BigDecimal.valueOf(1560000, 2)),
                InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(100, 2), pricePerUnit = BigDecimal.valueOf(220000.12)),
                // here there should be id=4 normally
                InputBudget(id = null, numberOfUnits = BigDecimal.valueOf(18.21), pricePerUnit = BigDecimal.valueOf(5000, 2))
            )
        )

        verify {
            projectPartnerBudgetStaffCostRepository
                .deleteAll(setOf(staffCost(id = 3, numberOfUnits = 15.18, pricePerUnit = 180.13)))
        }
    }

    @Test
    fun `save Budget_Any_ test maximum`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerBudgetStaffCostRepository.findAllByPartnerIdOrderByIdAsc(1) } returns emptyList()
        every { projectPartnerBudgetStaffCostRepository.deleteAll(any<List<ProjectPartnerBudgetStaffCost>>()) } answers {}
        every { projectPartnerBudgetStaffCostRepository.saveAll(any<List<ProjectPartnerBudgetStaffCost>>()) } returnsArgument 0

        val toBeSavedMaxNumberOfUnits = listOf(
            InputBudget(
                numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                pricePerUnit = BigDecimal.ONE)
        )

        assertThat(
            projectPartnerBudgetService.updateStaffCosts(1, 1, toBeSavedMaxNumberOfUnits)
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
            projectPartnerBudgetService.updateStaffCosts(1, 1, toBeSavedMaxPricePerUnit)
        ).overridingErrorMessage("pricePerUnit could be up to 999.999.999.999.999,99").isEqualTo(
            listOf(
                InputBudget(
                    numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_99L, 2),
                    pricePerUnit = BigDecimal.valueOf(100L, 2))
            )
        )
    }

    @Test
    fun `save Budget_Any_ more items than allowed`() {
        every { veryBigList.size } returns 301
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        assertThat(
            assertThrows<I18nValidationException> { projectPartnerBudgetService.updateStaffCosts(1, 1, veryBigList) }.i18nKey
        ).isEqualTo("project.partner.budget.max.allowed.reached")
    }

    @Test
    fun `save Budget_Any_ number out of range`() {
        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)

        assertThat(
            assertThrows<I18nValidationException> {
                projectPartnerBudgetService.updateStaffCosts(1, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.valueOf(999_999_999_999_999_991L, 3),
                        pricePerUnit = BigDecimal.ONE
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                projectPartnerBudgetService.updateStaffCosts(1, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.ONE,
                        pricePerUnit = BigDecimal.valueOf(999_999_999_999_999_991L, 3)
                    )
                ))
            }.i18nKey
        ).isEqualTo("project.partner.budget.number.out.of.range")

        assertThat(
            assertThrows<I18nValidationException> {
                projectPartnerBudgetService.updateStaffCosts(1, 1, listOf(
                    InputBudget(
                        numberOfUnits = BigDecimal.TEN,
                        pricePerUnit = BigDecimal.valueOf(100_000_000_000_000_00L, 2)
                    )
                ))
            }.i18nKey
        ).overridingErrorMessage("pricePerUnit * numberOfUnits together cannot be more then 999.999.999.999.999,99")
            .isEqualTo("project.partner.budget.number.out.of.range")
    }
    //endregion StaffCosts

    //region Travel

    @Test
    fun getProjectPartnerBudgetTravel() {
        val projectPartnerBudgetTravels = listOf(
            travel(id = 1, numberOfUnits = 2.00, pricePerUnit = 15600.00),
            travel(id = 2, numberOfUnits = 1.00, pricePerUnit = 220000.12),
            travel(id = 3, numberOfUnits = 15.18, pricePerUnit = 180.13)
        )

        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerBudgetTravelRepository.findAllByPartnerIdOrderByIdAsc(1) } returns projectPartnerBudgetTravels

        assertThat(projectPartnerBudgetService.getTravel(1, 1))
            .isEqualTo(
                listOf(
                    InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(2.00), pricePerUnit = BigDecimal.valueOf(15600.00)),
                    InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(1.00), pricePerUnit = BigDecimal.valueOf(220000.12)),
                    InputBudget(id = 3, numberOfUnits = BigDecimal.valueOf(15.18), pricePerUnit = BigDecimal.valueOf(180.13))
                )
            )
    }

    @Test
    fun `save BudgetTravel`() {
        val existing = listOf(
            travel(id = 1, numberOfUnits = 2000.00, pricePerUnit = 662.25),
            travel(id = 2, numberOfUnits = 18.00, pricePerUnit = 220_000_000.0),
            travel(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10) // to be removed
        )
        val toBeSaved = listOf(
            // updated existing
            InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(1500.00), pricePerUnit = BigDecimal.valueOf(773.36)),
            // no change for existing
            InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(18), pricePerUnit = BigDecimal.valueOf(220_000_000.0)),
            // new to be created
            InputBudget(id = null, numberOfUnits = BigDecimal.valueOf(550), pricePerUnit = BigDecimal.valueOf(10.0))
        )

        every { projectPartnerRepository.findFirstByProjectIdAndId(1, 1) } returns Optional.of(projectPartner)
        every { projectPartnerBudgetTravelRepository.findAllByPartnerIdOrderByIdAsc(1) } returns existing
        every { projectPartnerBudgetTravelRepository.deleteAll(any<List<ProjectPartnerBudgetTravel>>()) } answers {}
        every { projectPartnerBudgetTravelRepository.saveAll(any<List<ProjectPartnerBudgetTravel>>()) } returnsArgument 0

        assertThat(
            projectPartnerBudgetService.updateTravel(1, 1, toBeSaved)
        ).isEqualTo(
            listOf(
                InputBudget(id = 1, numberOfUnits = BigDecimal.valueOf(150000, 2), pricePerUnit = BigDecimal.valueOf(77336, 2)),
                InputBudget(id = 2, numberOfUnits = BigDecimal.valueOf(1800, 2), pricePerUnit = BigDecimal.valueOf(220_000_000_00, 2)),
                // here there should be id=4 normally
                InputBudget(id = null, numberOfUnits = BigDecimal.valueOf(55000, 2), pricePerUnit = BigDecimal.valueOf(1000, 2))
            )
        )

        verify {
            projectPartnerBudgetTravelRepository
                .deleteAll(setOf(travel(id = 3, numberOfUnits = 1.00, pricePerUnit = 1000_000.10)))
        }
    }
    //endregion Travel

}
