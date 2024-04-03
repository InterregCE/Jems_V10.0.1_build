package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getDeMinimisSection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.ProgrammeDataServiceImpl
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.*
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimisService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetDeMinimisSectionServiceTest : UnitTest() {

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

    @RelaxedMockK
    lateinit var versionPersistence: ProjectVersionPersistence

    @InjectMockKs
    lateinit var getContractingPartnerStateAidDeMinimisService: GetContractingPartnerStateAidDeMinimisService

    @BeforeEach
    fun setup() {
        every { partnerPersistence.getById(PARTNER_ID, LAST_APPROVED_VERSION) } returns leadPartner
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns LAST_APPROVED_VERSION
    }

    @Test
    fun `get minimis section when no data is saved and gber is selected`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every {
            partnerPersistence.getPartnerStateAid(
                PARTNER_ID,
                LAST_APPROVED_VERSION
            )
        } returns getStateAid(hasRisk = true, stateAidMeasure = gberStateAid)
        every {
            partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(
                PROJECT_ID,
                LAST_APPROVED_VERSION
            )
        } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        Assertions.assertThat(getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID))
            .isEqualTo(null)
    }

    @Test
    fun `get minimis section when no data is saved and minimis is selected`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns emptyDeMinimisModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every {
            partnerPersistence.getPartnerStateAid(
                PARTNER_ID,
                LAST_APPROVED_VERSION
            )
        } returns getStateAid(hasRisk = true, stateAidMeasure = minimisStateAid)
        every {
            partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(
                PROJECT_ID,
                LAST_APPROVED_VERSION
            )
        } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        Assertions.assertThat(getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            expectedEmptyDeMinimisSectionModel
        )
    }

    @Test
    fun `get de minimis section `() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = true,
            stateAidMeasure = minimisStateAid
        )
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, LAST_APPROVED_VERSION) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        Assertions.assertThat(getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            expectedDeMinimisSection
        )
    }

    @Test
    fun `get minimis section when gber is selected - should return null`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = true,
            stateAidMeasure = gberStateAid
        )
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, LAST_APPROVED_VERSION) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        Assertions.assertThat(getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            null
        )
    }

    @Test
    fun `get minimis section when there is no risk in state aid - should return null`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = false,
            stateAidMeasure = minimisStateAid
        )
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, LAST_APPROVED_VERSION) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        Assertions.assertThat(getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            expectedDeMinimisSection
        )
    }


    @Test
    fun `get minimis section when no measure is selected - should return null`() {
        every { contractingPartnerStateAidDeMinimisPersistence.findById(PARTNER_ID) } returns deMinimisModel
        every { getContractingMonitoringService.getProjectContractingMonitoring(PROJECT_ID) } returns getContractMonitoring()
        every { partnerPersistence.getPartnerStateAid(PARTNER_ID, LAST_APPROVED_VERSION) } returns getStateAid(
            hasRisk = true,
            stateAidMeasure = null
        )
        every { partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(PROJECT_ID, LAST_APPROVED_VERSION) } returns partnerFunds
        every { programmeDataService.getProgrammeDataOrThrow() } returns getProgrammeData()

        Assertions.assertThat(getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            null
        )
    }

}
