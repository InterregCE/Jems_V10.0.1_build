package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.Optional

class ProjectBudgetCoFinancingPersistenceTest {

    companion object {
        private const val PARTNER_ID = 1L

        private val fund1 = ProgrammeFundEntity(id = 10, abbreviation = "test01", selected = true)
        private val fund2 = ProgrammeFundEntity(id = 11, abbreviation = "test02", selected = true)

        private val funds: Set<ProgrammeFundEntity> = setOf(fund1, fund2)

        private val fund1Model = ProgrammeFund(id = fund1.id, abbreviation = fund1.abbreviation, selected = true)

        private val dummyCall = callWithId(10).copy(funds = funds)

        private val dummyProject = Project(
            id = 1,
            call = dummyCall,
            acronym = "Test Project",
            applicant = dummyCall.creator,
            projectStatus = ProjectStatus(id = 1, status = ProjectApplicationStatus.DRAFT, user = dummyCall.creator)
        )

        private val dummyPartner = ProjectPartnerEntity(
            id = PARTNER_ID,
            project = dummyProject,
            abbreviation = "test abbr",
            role = ProjectPartnerRole.LEAD_PARTNER,
            legalStatus = ProgrammeLegalStatus()
        )
    }

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    private lateinit var persistence: ProjectPartnerCoFinancingPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        persistence = ProjectPartnerCoFinancingPersistenceProvider(
            projectPartnerRepository,
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
        val dummyFinancing = setOf(
            ProjectPartnerCoFinancingEntity(id = 1, partnerId = PARTNER_ID, percentage = 25, programmeFund = fund1),
            ProjectPartnerCoFinancingEntity(id = 2, partnerId = PARTNER_ID, percentage = 75, programmeFund = null),
        )
        val dummyPartnerContributions = listOf(
            ProjectPartnerContributionEntity(id = 1, partnerId = PARTNER_ID, name = null, status = Public, amount = BigDecimal.TEN),
            ProjectPartnerContributionEntity(id = 2, partnerId = PARTNER_ID, name = "source01", status = Private, amount = BigDecimal.ONE),
        )
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(dummyPartner.copy(
            financing = dummyFinancing,
            partnerContributions = dummyPartnerContributions,
        ))

        val result = persistence.getCoFinancingAndContributions(PARTNER_ID)

        assertThat(result.partnerAbbreviation).isEqualTo(dummyPartner.abbreviation)
        assertThat(result.finances).containsExactlyInAnyOrder(
            ProjectPartnerCoFinancing(id = 1, fund = fund1Model, percentage = 25),
            ProjectPartnerCoFinancing(id = 2, fund = null, percentage = 75),
        )
        assertThat(result.partnerContributions).containsExactlyInAnyOrder(
            ProjectPartnerContribution(id = 1, name = null, status = Public, amount = BigDecimal.TEN, isPartner = true),
            ProjectPartnerContribution(id = 2, name = "source01", status = Private, amount = BigDecimal.ONE, isPartner = false),
        )
    }

    @Test
    fun `update CoFinancing and contribution`() {
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(dummyPartner)
        every { projectPartnerRepository.save(any()) } returnsArgument 0

        val toBeSavedFinancing = setOf(
            UpdateProjectPartnerCoFinancing(fundId = fund1.id, percentage = 30),
            UpdateProjectPartnerCoFinancing(fundId = null, percentage = 70),
        )
        val toBeSavedContributions = listOf(
            ProjectPartnerContribution(name = null, status = Public, amount = BigDecimal.TEN, isPartner = true),
            ProjectPartnerContribution(name = "source", status = Private, amount = BigDecimal.ONE, isPartner = false),
        )

        val result = persistence.updateCoFinancingAndContribution(
            partnerId = PARTNER_ID,
            finances = toBeSavedFinancing,
            partnerContributions = toBeSavedContributions
        )

        assertThat(result.partnerAbbreviation).isEqualTo(dummyPartner.abbreviation)
        assertThat(result.finances).containsExactlyInAnyOrder(
            ProjectPartnerCoFinancing(id = 0, fund = fund1Model, percentage = 30),
            ProjectPartnerCoFinancing(id = 0, fund = null, percentage = 70),
        )
        assertThat(result.partnerContributions).containsExactlyInAnyOrder(
            ProjectPartnerContribution(id = 0, name = null, status = Public, amount = BigDecimal.TEN, isPartner = true),
            ProjectPartnerContribution(id = 0, name = "source", status = Private, amount = BigDecimal.ONE, isPartner = false),
        )
    }

}
