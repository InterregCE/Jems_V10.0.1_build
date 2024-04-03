package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getGberSection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.*
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnerStateAidGberService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetGberSectionServiceTest : UnitTest() {

    @MockK
    lateinit var contractingPartnerStateAidGberPersistence: ContractingPartnerStateAidGberPersistence

    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerBudgetPerFundService: GetPartnerBudgetPerFundService

    @MockK
    lateinit var projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence

    @RelaxedMockK
    lateinit var versionPersistence: ProjectVersionPersistence

    @InjectMockKs
    lateinit var gberHelper: GberHelper

    @InjectMockKs
    lateinit var getContractingPartnerStateAidGberService: GetContractingPartnerStateAidGberService


    @BeforeEach
    fun setup() {
        every { partnerPersistence.getById(PARTNER_ID, LAST_APPROVED_VERSION) } returns getPartnerData()
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns LAST_APPROVED_VERSION
        every {
            projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(
                PARTNER_ID,
                LAST_APPROVED_VERSION
            )
        } returns getCofinancing()
        every {
            projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(
                PARTNER_ID,
                LAST_APPROVED_VERSION
            )
        } returns getSpfCofinancing()
        every {
            partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(
                PROJECT_ID,
                LAST_APPROVED_VERSION
            )
        } returns partnerFunds
    }

    @Test
    fun `get gber section when no data is saved and minimis is selected`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every {
            partnerPersistence.getPartnerStateAid(
                PARTNER_ID,
                LAST_APPROVED_VERSION
            )
        } returns getStateAid(hasRisk = true, stateAidMeasure = minimisStateAid)

        Assertions.assertThat(getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID)).isEqualTo(null)
    }

    @Test
    fun `get gber section when no data is saved and gber is selected`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns emptyGberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every {
            partnerPersistence.getPartnerStateAid(
                PARTNER_ID,
                LAST_APPROVED_VERSION
            )
        } returns getStateAid(hasRisk = true, stateAidMeasure = gberStateAid)

        Assertions.assertThat(getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID)).isEqualTo(
            expectedEmptyGberSectionModel
        )
    }

    @Test
    fun `get de gber section `() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = true,
            stateAidMeasure = gberStateAid
        )

        Assertions.assertThat(getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID)).isEqualTo(
            expectedGberSection
        )
    }

    @Test
    fun `get de gber section when there is no risk in state aid`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(hasRisk = false, gberStateAid)

        Assertions.assertThat(getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID)).isEqualTo(
            expectedGberSection
        )
    }

    @Test
    fun `get de gber section when no aid scheme is selected`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = true,
            stateAidMeasure = null
        )

        Assertions.assertThat(getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID)).isEqualTo(
            null
        )
    }


    @Test
    fun `get gber section when minimis is selected - should return null`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = true,
            stateAidMeasure = minimisStateAid
        )

        Assertions.assertThat(getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID)).isEqualTo(
            null
        )
    }

}
