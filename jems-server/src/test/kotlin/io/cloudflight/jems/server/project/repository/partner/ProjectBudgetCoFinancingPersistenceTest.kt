package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO.Public
import io.cloudflight.jems.server.call.callFundRateEntity
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerCoFinancingRepository
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.Optional

class ProjectBudgetCoFinancingPersistenceTest {

    companion object {
        private const val PARTNER_ID = 1L

        private val fund1 = callFundRateEntity(callWithId(10), 10L)
        private val fund2 = callFundRateEntity(callWithId(10), 11L)

        private val fund1Model = ProgrammeFund(id = fund1.setupId.programmeFund.id, selected = true)

        private fun dummyCall(): CallEntity {
            val call = callWithId(10)
            call.funds.clear()
            call.funds.addAll(setOf(fund1, fund2))
            return call
        }

        private fun dummyProject(): ProjectEntity {
            val call = dummyCall()
            return ProjectEntity(
                id = 1,
                call = call,
                acronym = "Test Project",
                applicant = call.creator,
                currentStatus = ProjectStatusHistoryEntity(
                    id = 1,
                    status = ApplicationStatus.DRAFT,
                    user = call.creator
                ),
            )
        }

        private val dummyPartner = ProjectPartnerEntity(
            id = PARTNER_ID,
            project = dummyProject(),
            abbreviation = "test abbr",
            role = ProjectPartnerRole.LEAD_PARTNER,
            legalStatus = ProgrammeLegalStatusEntity()
        )
    }

    @RelaxedMockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectPartnerCoFinancingRepository: ProjectPartnerCoFinancingRepository

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    private lateinit var persistence: ProjectPartnerCoFinancingPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPartnerCoFinancingPersistenceProvider(
            projectPartnerRepository,
            projectPartnerCoFinancingRepository,
            projectVersionUtils
        )
    }

    @Test
    fun `get available fund ids`() {
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(dummyPartner)
        assertThat(persistence.getAvailableFundIds(PARTNER_ID)).containsExactlyInAnyOrder(10, 11)
    }

    @Test
    fun `get available fund ids not existing`() {
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getAvailableFundIds(PARTNER_ID) }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

    @Test
    fun `get co financing and contributions`() {
        val dummyFinancing = mutableListOf(
            ProjectPartnerCoFinancingEntity(
                coFinancingFundId = ProjectPartnerCoFinancingFundId(
                    partnerId = PARTNER_ID,
                    orderNr = 1,
                ), percentage = BigDecimal.valueOf(24.5), programmeFund = fund1.setupId.programmeFund
            ),
            ProjectPartnerCoFinancingEntity(
                coFinancingFundId = ProjectPartnerCoFinancingFundId(
                    partnerId = PARTNER_ID,
                    orderNr = 2,
                ), percentage = BigDecimal.valueOf(74.5), programmeFund = null
            )
        )
        val dummyPartnerContributions = listOf(
            ProjectPartnerContribution(
                id = 1,
                name = null,
                status = Public,
                amount = BigDecimal.TEN,
                isPartner = true
            ),
            ProjectPartnerContribution(
                id = 2,
                name = "source01",
                status = Private,
                amount = BigDecimal.ONE,
                isPartner = false,
            )
        )
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(
            dummyPartner.copy(
                newPartnerContributions = dummyPartnerContributions
            )
        )
        every { projectPartnerCoFinancingRepository.findAllByCoFinancingFundIdPartnerId(PARTNER_ID) } returns dummyFinancing

        val result = persistence.getCoFinancingAndContributions(PARTNER_ID, null)

        assertThat(result.partnerAbbreviation).isEqualTo(dummyPartner.abbreviation)
        assertThat(result.finances).containsExactlyInAnyOrder(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = fund1Model,
                percentage = BigDecimal.valueOf(24.5)
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fund = null,
                percentage = BigDecimal.valueOf(74.5)
            )
        )
        assertThat(result.partnerContributions).containsExactlyInAnyOrder(
            ProjectPartnerContribution(id = 1, name = null, status = Public, amount = BigDecimal.TEN, isPartner = true),
            ProjectPartnerContribution(
                id = 2,
                name = "source01",
                status = Private,
                amount = BigDecimal.ONE,
                isPartner = false
            )
        )
    }

    @Test
    fun `update CoFinancing and contribution`() {
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(dummyPartner)
        every { projectPartnerRepository.save(any()) } returnsArgument 0
        every { projectPartnerCoFinancingRepository.deleteByCoFinancingFundIdPartnerId(PARTNER_ID) } answers { }
        every { projectPartnerCoFinancingRepository.saveAll(any<HashSet<ProjectPartnerCoFinancingEntity>>()) } answers {
            val destination = firstArg<HashSet<ProjectPartnerCoFinancingEntity>>()
            destination.map { it }
        }

        val toBeSavedFinancing = listOf(
            UpdateProjectPartnerCoFinancing(
                fundId = fund1.setupId.programmeFund.id,
                percentage = BigDecimal.valueOf(29.5)
            ),
            UpdateProjectPartnerCoFinancing(
                fundId = null,
                percentage = BigDecimal.valueOf(69.5)
            )
        )
        val toBeSavedContributions = listOf(
            ProjectPartnerContribution(name = null, status = Public, amount = BigDecimal.TEN, isPartner = true),
            ProjectPartnerContribution(name = "source", status = Private, amount = BigDecimal.ONE, isPartner = false)
        )

        val result = persistence.updateCoFinancingAndContribution(
            partnerId = PARTNER_ID,
            finances = toBeSavedFinancing,
            partnerContributions = toBeSavedContributions
        )

        assertThat(result.partnerAbbreviation).isEqualTo(dummyPartner.abbreviation)
        assertThat(result.finances).containsExactlyInAnyOrder(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = fund1Model,
                percentage = BigDecimal.valueOf(29.5)
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fund = null,
                percentage = BigDecimal.valueOf(69.5)
            )
        )
        assertThat(result.partnerContributions).containsExactlyInAnyOrder(
            ProjectPartnerContribution(id = 0, name = null, status = Public, amount = BigDecimal.TEN, isPartner = true),
            ProjectPartnerContribution(
                id = 0,
                name = "source",
                status = Private,
                amount = BigDecimal.ONE,
                isPartner = false
            )
        )
    }

}
