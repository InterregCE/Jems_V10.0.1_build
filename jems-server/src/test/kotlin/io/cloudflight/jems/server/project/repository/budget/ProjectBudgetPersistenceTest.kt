package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.partnerWithId
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumRow
import io.cloudflight.jems.server.project.entity.partner.PartnerSimpleRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetRow
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetView
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetUnitCostRepository
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

class ProjectBudgetPersistenceTest {

    companion object {
        private const val PARTNER_ID = 1L
        private val PARTNER_IDS = setOf(PARTNER_ID)
        private val testBudgets = listOf(ProjectPartnerBudgetView(partnerId = PARTNER_ID, sum = BigDecimal.TEN))
        private val expectedBudget = ProjectPartnerCost(partnerId = PARTNER_ID, sum = BigDecimal.TEN)

        private const val version = "3.0"
        private val timestamp = Timestamp.valueOf(LocalDateTime.now())
        private val mockPBRow: ProjectPartnerBudgetRow = mockk()
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
        every { mockPBRow.partnerId } returns PARTNER_ID
        every { mockPBRow.sum } returns BigDecimal.TEN
    }

    @Test
    fun getStaffCosts() {
        every { budgetStaffCostRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getStaffCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getStaffCostsHistoric() {
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { budgetStaffCostRepository.sumForAllPartnersAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockPBRow)

        assertThat(projectBudgetPersistence.getStaffCosts(PARTNER_IDS, 1L, version))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getTravelCosts() {
        every { budgetTravelRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getTravelCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getTravelCostsHistoric() {
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { budgetTravelRepository.sumForAllPartnersAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockPBRow)

        assertThat(projectBudgetPersistence.getTravelCosts(PARTNER_IDS, 1L, version))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getExternalCosts() {
        every { budgetExternalRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getExternalCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getExternalCostsHistoric() {
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { budgetExternalRepository.sumForAllPartnersAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockPBRow)

        assertThat(projectBudgetPersistence.getExternalCosts(PARTNER_IDS, 1L, version))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getEquipmentCosts() {
        every { budgetEquipmentRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getEquipmentCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getEquipmentCostsHistoric() {
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { budgetEquipmentRepository.sumForAllPartnersAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockPBRow)

        assertThat(projectBudgetPersistence.getEquipmentCosts(PARTNER_IDS, 1L, version))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getInfrastructureCosts() {
        every { budgetInfrastructureRepository.sumForAllPartners(PARTNER_IDS) } returns testBudgets
        assertThat(projectBudgetPersistence.getInfrastructureCosts(PARTNER_IDS, 1L))
            .containsExactlyInAnyOrder(expectedBudget)
    }

    @Test
    fun getInfrastructureHistoric() {
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { budgetInfrastructureRepository.sumForAllPartnersAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockPBRow)

        assertThat(projectBudgetPersistence.getInfrastructureCosts(PARTNER_IDS, 1L, version))
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
                partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
                nace = NaceGroupLevel.A,
                pic = "034",
                otherIdentifierNumber = "id-12",
                sortNumber = 1,
                addresses = setOf(ProjectPartnerAddressEntity(
                    addressId = ProjectPartnerAddressId(5, ProjectPartnerAddressType.Organization),
                    address = AddressEntity(country = "SK")
                ))
            )
        )
        every { projectPartnerRepository.findTop30ByProjectId(eq(1), any()) } returns partners
        assertThat(projectBudgetPersistence.getPartnersForProjectId(1))
            .containsExactlyInAnyOrder(
                ProjectPartnerSummary(
                    id = 5,
                    abbreviation = "partner",
                    role = ProjectPartnerRole.LEAD_PARTNER,
                    sortNumber = 1,
                    country = "SK"
                )
            )
    }

    @Test
    fun getPartnersForProjectIdHistoric() {
        val mockPRow: PartnerSimpleRow = mockk()
        every { mockPRow.id } returns PARTNER_ID
        every { mockPRow.abbreviation } returns "abbreviation"
        every { mockPRow.role } returns ProjectPartnerRole.LEAD_PARTNER
        every { mockPRow.sortNumber } returns 1
        every { mockPRow.country } returns "AT"
        every { mockPRow.region } returns "nutsRegion3"
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { projectPartnerRepository.findTop30ByProjectIdSortBySortNumberAsOfTimestamp(1L, timestamp) } returns listOf(mockPRow)

        assertThat(projectBudgetPersistence.getPartnersForProjectId(1L, version))
            .containsExactly(ProjectPartnerSummary(
                id = PARTNER_ID,
                abbreviation = "abbreviation",
                role = ProjectPartnerRole.LEAD_PARTNER,
                sortNumber = 1,
                country = "AT",
                region = "nutsRegion3"
            ))
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
    fun getLumpSumContributionPerPartnerHistoric() {
        val mockLSRow: ProjectLumpSumPerPartnerSumRow = mockk()
        every { mockLSRow.partnerId } returns PARTNER_ID
        every { mockLSRow.sum } returns BigDecimal.TEN
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { projectPartnerLumpSumRepository.sumLumpSumsPerPartnerAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockLSRow)

        assertThat(projectBudgetPersistence.getLumpSumContributionPerPartner(PARTNER_IDS, 1L, version))
            .containsExactlyInAnyOrderEntriesOf(mapOf(
                PARTNER_ID to BigDecimal.TEN
            ))
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


    @Test
    fun getBudgetUnitCostsPerPartnerHistoric() {
        every { projectVersionRepo.findTimestampByVersion(1L, version) } returns timestamp
        every { projectPartnerUnitCostRepository.sumForAllPartnersAsOfTimestamp(PARTNER_IDS, timestamp) } returns listOf(mockPBRow)

        assertThat(projectBudgetPersistence.getUnitCostsPerPartner(PARTNER_IDS, 1L, version))
            .containsExactlyInAnyOrderEntriesOf(mapOf(
                PARTNER_ID to BigDecimal.TEN
            ))
    }
}
