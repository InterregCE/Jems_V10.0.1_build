package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.updateGberSection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.gber.toModel
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.GberHelper
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.PARTNER_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.PROJECT_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.expectedGberSection
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.gberEntity
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.gberModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getCofinancing
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getContractMonitoring
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getPartnerData
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getSpfCofinancing
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.partnerFunds
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.updateStateAidGberSection.UpdateContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateGberSectionTest : UnitTest() {

    @MockK
    lateinit var contractingPartnerStateAidGberPersistence: ContractingPartnerStateAidGberPersistence

    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @MockK
    lateinit var partnerBudgetPerFundService: GetPartnerBudgetPerFundService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence

    @MockK
    lateinit var validator: GeneralValidatorService

    @MockK
    lateinit var  projectContractingSectionLockPersistence: ProjectContractingSectionLockPersistence

    @MockK
    lateinit var contractingPartnerLockPersistence: ContractingPartnerLockPersistence

    @InjectMockKs
    lateinit var contractingValidator: ContractingValidator

    @InjectMockKs
    lateinit var gberHelper: GberHelper

    @InjectMockKs
    lateinit var updateContractingPartnerStateAidGber: UpdateContractingPartnerStateAidGber

    @Test
    fun updateGberData() {
        every {
            contractingPartnerStateAidGberPersistence.saveGber(
                PARTNER_ID,
                gberEntity.toModel()
            )
        } returns gberEntity
        every { contractingPartnerLockPersistence.isLocked(PARTNER_ID) } returns false
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds

        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        assertThat(updateContractingPartnerStateAidGber.updateGberSection(PARTNER_ID, gberModel)).isEqualTo(
            expectedGberSection
        )
    }

    @Test
    fun `update gber data when partner section is locked`() {
        every {
            contractingPartnerStateAidGberPersistence.saveGber(
                PARTNER_ID,
                gberEntity.toModel()
            )
        } returns gberEntity
        every { contractingPartnerLockPersistence.isLocked(PARTNER_ID) } returns true
        every { contractingPartnerStateAidGberPersistence.findById(PARTNER_ID) } returns gberModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getById(PARTNER_ID) } returns getPartnerData()
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds

        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(PARTNER_ID) } returns getCofinancing()
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(PARTNER_ID) } returns getSpfCofinancing()

        assertThrows<ContractingModificationDeniedException> { updateContractingPartnerStateAidGber.updateGberSection(PARTNER_ID, gberModel) }
    }
}
