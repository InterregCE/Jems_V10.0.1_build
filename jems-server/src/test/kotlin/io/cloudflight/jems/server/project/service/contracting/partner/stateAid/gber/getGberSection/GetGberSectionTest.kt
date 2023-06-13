package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getGberSection

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.GberHelper
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.PARTNER_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.PROJECT_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.emptyGberModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.expectedEmptyGberSectionModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.expectedGberSection
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.gberModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.gberStateAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getCofinancing
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getContractMonitoring
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getPartnerData
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getSpfCofinancing
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.minimisStateAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.partnerFunds
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetGberSectionTest : UnitTest() {

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

    @InjectMockKs
    lateinit var gberHelper: GberHelper

    @InjectMockKs
    lateinit var getContractingPartnerStateAidGber: GetContractingPartnerStateAidGber

    @Test
    fun `get gber section when no data is saved and minimis is selected`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = minimisStateAid)
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(null)
    }

    @Test
    fun `get gber section when no data is saved and gber is selected`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns emptyGberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = gberStateAid)
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(
            expectedEmptyGberSectionModel
        )
    }

    @Test
    fun `get de gber section `() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = gberStateAid)
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(
            expectedGberSection
        )
    }

    @Test
    fun `get de gber section when there is no risk in state aid`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = false, gberStateAid)
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(
            null
        )
    }

    @Test
    fun `get de gber section when no aid scheme is selected`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = null)
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(
            null
        )
    }


    @Test
    fun `get gber section when minimis is selected - should return null`() {
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = minimisStateAid)
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(
            null
        )
    }
}
