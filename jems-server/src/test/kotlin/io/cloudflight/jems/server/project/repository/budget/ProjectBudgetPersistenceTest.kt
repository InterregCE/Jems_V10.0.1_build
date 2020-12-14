package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.*
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import java.math.BigDecimal

class ProjectBudgetPersistenceTest {

    companion object {
        private const val PARTNER_ID = 1L
        private val PARTNER_IDS = setOf(PARTNER_ID)
        private val testBudgets = listOf(ProjectPartnerBudgetEntity(partnerId = PARTNER_ID, sum = BigDecimal.TEN))
        private val expectedBudget = ProjectPartnerCost(partnerId = PARTNER_ID, sum = BigDecimal.TEN)
    }

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository

    @MockK
    lateinit var budgetTravelRepository: ProjectPartnerBudgetTravelRepository

    @MockK
    lateinit var budgetExternalRepository: ProjectPartnerBudgetExternalRepository

    @MockK
    lateinit var budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository

    @MockK
    lateinit var budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    private lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectBudgetPersistence = ProjectBudgetPersistenceProvider(
            projectPartnerRepository,
            budgetStaffCostRepository,
            budgetTravelRepository,
            budgetExternalRepository,
            budgetEquipmentRepository,
            budgetInfrastructureRepository
        )
    }

    @Test
    fun getStaffCosts() {
        every { budgetStaffCostRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getStaffCosts(PARTNER_IDS))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getTravelCosts() {
        every { budgetTravelRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getTravelCosts(PARTNER_IDS))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getExternalCosts() {
        every { budgetExternalRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getExternalCosts(PARTNER_IDS))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getEquipmentCosts() {
        every { budgetEquipmentRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getEquipmentCosts(PARTNER_IDS))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getInfrastructureCosts() {
        every { budgetInfrastructureRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getInfrastructureCosts(PARTNER_IDS))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getPartnersForProjectId() {
        val partners = listOf(
            ProjectPartnerEntity(
                id = 5,
                project = ProjectPartnerTestUtil.project,
                abbreviation = "partner",
                role = ProjectPartnerRole.LEAD_PARTNER,
                legalStatus = ProgrammeLegalStatus(1, "description"),
                sortNumber = 1,
                addresses = setOf(ProjectPartnerAddress(
                    addressId = ProjectPartnerAddressId(5, ProjectPartnerAddressType.Organization),
                    address = AddressEntity(country = "SK")
                ))
            )
        )
        every { projectPartnerRepository.findTop30ByProjectId(eq(1), any<Sort>()) } returns partners
        assertThat(projectBudgetPersistence.getPartnersForProjectId(1))
            .containsExactlyInAnyOrder(
                ProjectPartner(
                    id = 5,
                    abbreviation = "partner",
                    role = ProjectPartnerRole.LEAD_PARTNER,
                    sortNumber = 1,
                    country = "SK"
                )
            )
    }

}
