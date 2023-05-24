package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getDeMinimisSection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.ProgrammeDataServiceImpl
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.PARTNER_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.PROJECT_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.deMinimisModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.emptyDeMinimisModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.expectedDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.expectedEmptyDeMinimisSectionModel
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.gberStateAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getContractMonitoring
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getProgrammeData
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.leadPartner
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.minimisStateAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.partnerFunds
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetDeMinimisSectionTest : UnitTest() {

    @MockK
    lateinit var contractingPartnerStateAidDeMinimisPersistence: ContractingPartnerStateAidDeMinimisPersistence

    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerBudgetPerFundService: GetPartnerBudgetPerFundService

    @MockK
    lateinit var programmeDataService: ProgrammeDataServiceImpl

    @InjectMockKs
    lateinit var getContractingPartnerStateAidDeMinimis: GetContractingPartnerStateAidDeMinimis

    @Test
    fun `get minimis section when no data is saved and gber is selected`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { partnerPersistence.getById(PARTNER_ID) } returns leadPartner
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = gberStateAid)
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(null)
    }

    @Test
    fun `get minimis section when no data is saved and minimis is selected`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns emptyDeMinimisModel
        every { partnerPersistence.getById(PARTNER_ID) } returns leadPartner
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = minimisStateAid)
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            expectedEmptyDeMinimisSectionModel
        )
    }

    @Test
    fun `get de minimis section `() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { partnerPersistence.getById(PARTNER_ID) } returns leadPartner
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = minimisStateAid)
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            expectedDeMinimisSection
        )
    }

    @Test
    fun `get minimis section when gber is selected - should return null`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { partnerPersistence.getById(PARTNER_ID) } returns leadPartner
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = gberStateAid)
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            null
        )
    }

    @Test
    fun `get minimis section when there is no risk in state aid - should return null`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { partnerPersistence.getById(PARTNER_ID) } returns leadPartner
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = false, stateAidMeasure = minimisStateAid)
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            null
        )
    }


    @Test
    fun `get minimis section when no measure is selected - should return null`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { partnerPersistence.getById(PARTNER_ID) } returns leadPartner
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID) } returns getStateAid(hasRisk = true, stateAidMeasure = null)
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, null) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            null
        )
    }
}
