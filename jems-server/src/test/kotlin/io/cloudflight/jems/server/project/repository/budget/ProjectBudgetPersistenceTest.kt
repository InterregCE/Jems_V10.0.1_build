package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.partnerWithId
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetView
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.*
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
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
        private val testBudgets = listOf(ProjectPartnerBudgetView(partnerId = PARTNER_ID, sum = BigDecimal.TEN))
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

    @MockK
    lateinit var projectPartnerLumpSumRepository: ProjectPartnerLumpSumRepository

    @MockK
    lateinit var projectPartnerUnitCostRepository: ProjectPartnerBudgetUnitCostRepository

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    private lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        projectBudgetPersistence = ProjectBudgetPersistenceProvider(
            projectPartnerRepository,
            budgetStaffCostRepository,
            budgetTravelRepository,
            budgetExternalRepository,
            budgetEquipmentRepository,
            budgetInfrastructureRepository,
            projectPartnerUnitCostRepository,
            projectPartnerLumpSumRepository,
            projectVersionUtils
        )
    }

    @Test
    fun getStaffCosts() {
        every { budgetStaffCostRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getStaffCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getTravelCosts() {
        every { budgetTravelRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getTravelCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getExternalCosts() {
        every { budgetExternalRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getExternalCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getEquipmentCosts() {
        every { budgetEquipmentRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getEquipmentCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getInfrastructureCosts() {
        every { budgetInfrastructureRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getInfrastructureCosts(PARTNER_IDS, 1L))
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
                legalStatus = ProgrammeLegalStatusEntity(1),
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

    @Test
    fun getLumpSumContributionPerPartner() {
        every { projectPartnerLumpSumRepository.sumLumpSumsPerPartner(setOf(PARTNER_ID)) } returns listOf(
            ProjectLumpSumPerPartnerSumEntity(
                partner = partnerWithId(PARTNER_ID),
                sum = BigDecimal.TEN,
            ),
        )
        assertThat(projectBudgetPersistence.getLumpSumContributionPerPartner(setOf(PARTNER_ID), 1L)).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                PARTNER_ID to BigDecimal.TEN
            )
        )
    }

    @Test
    fun getBudgetUnitCostsPerPartner() {
        val id = PARTNER_ID
        every { projectPartnerUnitCostRepository.sumForAllPartners(setOf(PARTNER_ID)) } returns listOf(
            ProjectPartnerBudgetView(
                partnerId = PARTNER_ID,
                sum = BigDecimal.TEN,
            ),
        )
        assertThat(projectBudgetPersistence.getUnitCostsPerPartner(setOf(id), 1L)).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                PARTNER_ID to BigDecimal.TEN
            )
        )
    }

}
