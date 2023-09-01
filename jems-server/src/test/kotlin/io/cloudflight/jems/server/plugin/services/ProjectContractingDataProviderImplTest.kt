package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.contracting.contracts.ProjectContractInfoData
import io.cloudflight.jems.plugin.contract.models.project.contracting.management.ManagementTypeData
import io.cloudflight.jems.plugin.contract.models.project.contracting.management.ProjectContractingManagementData
import io.cloudflight.jems.plugin.contract.models.project.contracting.monitoring.ContractingMonitoringAddDateData
import io.cloudflight.jems.plugin.contract.models.project.contracting.monitoring.ContractingMonitoringExtendedOptionData
import io.cloudflight.jems.plugin.contract.models.project.contracting.monitoring.ContractingMonitoringOptionData
import io.cloudflight.jems.plugin.contract.models.project.contracting.monitoring.ProjectContractingMonitoringData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.ProjectContractingPartnersSummaryData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.bankingDetails.ProjectContractingPartnerBankingDetailsData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.beneficialOwner.ProjectContractingPartnerBeneficialOwnerData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.deMinimis.BaseForGrantingData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.deMinimis.MemberStateForGrantingData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.deMinimis.ProjectContractingPartnerStateAidDeMinimisData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.documentsLocation.ProjectContractingPartnerDocumentsLocationData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.gber.LocationInAssistedAreaData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.gber.ProjectContractingPartnerStateAidGberData
import io.cloudflight.jems.plugin.contract.models.project.contracting.reporting.ContractingDeadlineTypeData
import io.cloudflight.jems.plugin.contract.models.project.contracting.reporting.ProjectContractingReportingScheduleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.NaceGroupLevelData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.PartnerBudgetPerFundData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfoService
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementService
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.*
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsService
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners.GetContractingPartnerBeneficialOwnersService
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation.GetContractingPartnerDocumentsLocationService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.COUNTRY_SK
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.COUNTRY_SK_CODE
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.PARTNER_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimisService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.expectedDateOfGrantingAid
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.fund1
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getExpectedPartnerFunds
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnerStateAidGberService
import io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting.GetContractingReportingService
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ProjectContractingDataProviderImplTest: UnitTest() {

    companion object {
        private val projectContractingInfo = ProjectContractInfo(
            projectStartDate = LocalDate.of(2022, 8, 1),
            projectEndDate = LocalDate.of(2023, 8, 1),
            website = "tgci.gov",
            subsidyContractDate = LocalDate.of(2022, 8, 22),
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        )

        private fun projectSummary(applicationStatus: ApplicationStatus) = ProjectSummary(
            id = 1L,
            customIdentifier = "TGCI",
            callId = 1L,
            callName = "Test Contract Info",
            acronym = "TCI",
            status = applicationStatus,
            firstSubmissionDate = ZonedDateTime.parse("2022-06-20T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2022-07-20T10:00:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1"
        )

        private val projectContractMonitoring = ProjectContractingMonitoring(
            projectId = 1L,
            startDate = LocalDate.of(2022, 8, 1),
            endDate = LocalDate.of(2023, 8, 1),
            typologyProv94 = ContractingMonitoringExtendedOption.Partly,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOption.Yes,
            typologyProv95Comment = "typologyProv95Comment",
            typologyStrategic = ContractingMonitoringOption.No,
            typologyStrategicComment = "typologyStrategicComment",
            typologyPartnership = ContractingMonitoringOption.Yes,
            typologyPartnershipComment = "typologyPartnershipComment",
            addDates = listOf(
                ProjectContractingMonitoringAddDate(
                    projectId = 1L,
                    number = 1,
                    entryIntoForceDate = LocalDate.of(2022, 7, 6),
                    comment = "comment"
                ),
                ProjectContractingMonitoringAddDate(
                    projectId = 1L,
                    number = 2,
                    entryIntoForceDate = LocalDate.of(2022, 8, 22),
                    comment = "comment"
                )
            ),
            fastTrackLumpSums = null,
            dimensionCodes = emptyList()
        )

        private val projectManagers = listOf(
            ProjectContractingManagement(
                projectId = 1L,
                managementType = ManagementType.ProjectManager,
                title = "Mr",
                firstName = "Test",
                lastName = "UserOne",
                email = "testuser1@jems.eu",
                telephone = "9212347801"
            ),
            ProjectContractingManagement(
                projectId = 1L,
                managementType = ManagementType.CommunicationManager,
                title = "Mr",
                firstName = "Test",
                lastName = "UserTwo",
                email = "testuser2@jems.eu",
                telephone = "8271929316"
            ),
            ProjectContractingManagement(
                projectId = 1L,
                managementType = ManagementType.FinanceManager,
                title = "Mrs",
                firstName = "Test",
                lastName = "UserThree",
                email = "testuser2@jems.eu",
                telephone = "56121347893"
            ),
        )

        private val bankingDetails = ContractingPartnerBankingDetails(
            partnerId = 1L,
            accountHolder = "Test",
            accountNumber = "123",
            accountIBAN = "RO99BT123",
            accountSwiftBICCode = "MIDT123",
            bankName = "BT",
            streetName = "Test",
            streetNumber = "42A",
            postalCode = "000123",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)",
            internalReferenceNr = "12345",
            city = "Wien"
        )

        private val beneficialOwner1 = ContractingPartnerBeneficialOwner(
            id = 18L,
            partnerId = 20L,
            firstName = "Test1",
            lastName = "Sample2",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwner2 = ContractingPartnerBeneficialOwner(
            id = 19L,
            partnerId = 20L,
            firstName = "Test2",
            lastName = "Sample2",
            vatNumber = "102030",
            birth = null
        )

        private val documentsLocation = ContractingPartnerDocumentsLocation(
            id = 18L,
            partnerId = 1L,
            firstName = "Test",
            lastName = "Sample",
            nutsThreeRegionCode = "",
            city = "Istanbul",
            countryCode = "TR",
            nutsTwoRegionCode = "",
            country = "Turkey",
            emailAddress = "sample@mail.com",
            locationNumber = "12A",
            homepage = "homepage",
            institutionName = "Sample name",
            nutsThreeRegion = "",
            nutsTwoRegion = "",
            postalCode = "34000",
            street = "Sample street",
            telephoneNo = "1020304050",
            title = "Title"
        )

        private val partnerSummary1 = ProjectPartnerSummary(
            id = 99L,
            abbreviation = "test 1",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = 1
        )

        private val partnerSummary2 = ProjectPartnerSummary(
            id = 100L,
            abbreviation = "test 2",
            active = true,
            role = ProjectPartnerRole.PARTNER,
            sortNumber = 2
        )

        private val reportingSchedule = ProjectContractingReportingSchedule(
            id = 55L,
            type = ContractingDeadlineType.Content,
            periodNumber = 1,
            date = LocalDate.of(2023, 7, 6),
            comment = "sample comment",
            number = 1,
            linkedSubmittedProjectReportNumbers = setOf(),
            linkedDraftProjectReportNumbers = setOf()
        )

        private val stateAidDeMinimisSection = ContractingPartnerStateAidDeMinimisSection(
            partnerId = 1L,
            dateOfGrantingAid = LocalDate.of(2022, 1, 5),
            amountGrantingAid = BigDecimal.TEN,
            selfDeclarationSubmissionDate = ZonedDateTime.of(2022,1,30,15,10,10,10, ZoneId.systemDefault()),
            baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
            aidGrantedByCountry = "Österreich",
            memberStatesGranting = setOf(
                MemberStateForGranting(
                    partnerId = 1L,
                    country = "Slovakia",
                    countryCode = "SK",
                    selected = false,
                    amountInEur = BigDecimal.ONE
                )
            ),
            comment = "Test comment"
        )

        private val stateAidGberSection = ContractingPartnerStateAidGberSection(
            partnerId = 1L,
            dateOfGrantingAid = LocalDate.of(2022, 1, 5),
            partnerFunds = setOf(
                PartnerBudgetPerFund(
                    fund = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF),
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                )
            ),
            amountGrantingAid = BigDecimal.TEN,
            naceGroupLevel = NaceGroupLevel.A_01_1,
            aidIntensity = BigDecimal.TEN,
            locationInAssistedArea = LocationInAssistedArea.A_AREA,
            comment = "Test comment"
        )
    }

    @MockK
    lateinit var getContractInfoService: GetContractInfoService

    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @MockK
    lateinit var getContractingManagementService: GetContractingManagementService

    @MockK
    lateinit var getContractingPartnerBankingDetailsService: GetContractingPartnerBankingDetailsService

    @MockK
    lateinit var getContractingPartnerBeneficialOwnersService: GetContractingPartnerBeneficialOwnersService

    @MockK
    lateinit var getContractingPartnerDocumentsLocationService: GetContractingPartnerDocumentsLocationService

    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var getContractingReportingService: GetContractingReportingService

    @MockK
    lateinit var getContractingPartnerStateAidDeMinimisService: GetContractingPartnerStateAidDeMinimisService

    @MockK
    lateinit var getContractingPartnerStateAidGberService: GetContractingPartnerStateAidGberService

    @InjectMockKs
    lateinit var dataProvider: ProjectContractingDataProviderImpl

    @Test
    fun getContractInfo() {
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(ApplicationStatus.APPROVED)
        mockkObject(ContractingValidator.Companion)
        every { ContractingValidator.validateProjectStepAndStatus(projectSummary(ApplicationStatus.APPROVED)) } just Runs
        every { getContractingMonitoringService.getProjectContractingMonitoring(1L) } returns projectContractMonitoring
        every { getContractInfoService.getContractInfo(1L) } returns projectContractingInfo

        Assertions.assertThat(dataProvider.getContractInfo(1L)).isEqualTo(
            ProjectContractInfoData(
                projectStartDate = LocalDate.of(2022, 8, 1),
                projectEndDate = LocalDate.of(2023, 8, 1),
                website = "tgci.gov",
                subsidyContractDate = LocalDate.of(2022, 8, 22),
                partnershipAgreementDate = LocalDate.of(2022, 9, 12)
            )
        )
    }

    @Test
    fun getContractingManagementInfo() {
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(ApplicationStatus.APPROVED)
        mockkObject(ContractingValidator.Companion)
        every { ContractingValidator.validateProjectStepAndStatus(projectSummary(ApplicationStatus.APPROVED)) } returns Unit
        every { getContractingManagementService.getContractingManagement(1L) } returns projectManagers

        Assertions.assertThat(dataProvider.getContractingManagementInfo(1L))
            .isEqualTo(listOf(
                ProjectContractingManagementData(
                    projectId = 1L,
                    managementType = ManagementTypeData.ProjectManager,
                    title = "Mr",
                    firstName = "Test",
                    lastName = "UserOne",
                    email = "testuser1@jems.eu",
                    telephone = "9212347801"
                ),
                ProjectContractingManagementData(
                    projectId = 1L,
                    managementType = ManagementTypeData.CommunicationManager,
                    title = "Mr",
                    firstName = "Test",
                    lastName = "UserTwo",
                    email = "testuser2@jems.eu",
                    telephone = "8271929316"
                ),
                ProjectContractingManagementData(
                    projectId = 1L,
                    managementType = ManagementTypeData.FinanceManager,
                    title = "Mrs",
                    firstName = "Test",
                    lastName = "UserThree",
                    email = "testuser2@jems.eu",
                    telephone = "56121347893"
                )
            ))
    }

    @Test
    fun getContractingMonitoring() {
        every { getContractingMonitoringService.getContractingMonitoring(1L) } returns projectContractMonitoring
        Assertions.assertThat(dataProvider.getContractingMonitoring(1L)).isEqualTo(
            ProjectContractingMonitoringData(
                projectId = 1L,
                startDate = LocalDate.of(2022, 8, 1),
                endDate = LocalDate.of(2023, 8, 1),
                typologyProv94 = ContractingMonitoringExtendedOptionData.Partly,
                typologyProv94Comment = "typologyProv94Comment",
                typologyProv95 = ContractingMonitoringExtendedOptionData.Yes,
                typologyProv95Comment = "typologyProv95Comment",
                typologyStrategic = ContractingMonitoringOptionData.No,
                typologyStrategicComment = "typologyStrategicComment",
                typologyPartnership = ContractingMonitoringOptionData.Yes,
                typologyPartnershipComment = "typologyPartnershipComment",
                addDates = listOf(
                    ContractingMonitoringAddDateData(
                        projectId = 1L,
                        number = 1,
                        entryIntoForceDate = LocalDate.of(2022, 7, 6),
                        comment = "comment"
                    ),
                    ContractingMonitoringAddDateData(
                        projectId = 1L,
                        number = 2,
                        entryIntoForceDate = LocalDate.of(2022, 8, 22),
                        comment = "comment"
                    )
                ),
                fastTrackLumpSums = null,
                dimensionCodes = emptyList()
            )
        )
    }

    @Test
    fun getContractingPartnerBankingDetails() {
        every { getContractingPartnerBankingDetailsService.getBankingDetails(1L) } returns bankingDetails
        Assertions.assertThat(dataProvider.getContractingPartnerBankingDetails(1L)).isEqualTo(
            ProjectContractingPartnerBankingDetailsData(
                partnerId = 1L,
                accountHolder = "Test",
                accountNumber = "123",
                accountIBAN = "RO99BT123",
                accountSwiftBICCode = "MIDT123",
                bankName = "BT",
                streetName = "Test",
                streetNumber = "42A",
                postalCode = "000123",
                country = "Österreich (AT)",
                nutsTwoRegion = "Wien (AT13)",
                nutsThreeRegion = "Wien (AT130)",
                internalReferenceNr = "12345",
                city = "Wien"
            )
        )
    }

    @Test
    fun getContractingPartnerBeneficialOwners() {
        every { getContractingPartnerBeneficialOwnersService.getBeneficialOwners(1L) } returns listOf(
            beneficialOwner1,
            beneficialOwner2
        )
        Assertions.assertThat(dataProvider.getContractingPartnerBeneficialOwners(1L)).isEqualTo(
            listOf(
                ProjectContractingPartnerBeneficialOwnerData(
                    id = 18L,
                    partnerId = 20L,
                    firstName = "Test1",
                    lastName = "Sample2",
                    vatNumber = "123456",
                    birth = null
                ),
                ProjectContractingPartnerBeneficialOwnerData(
                    id = 19L,
                    partnerId = 20L,
                    firstName = "Test2",
                    lastName = "Sample2",
                    vatNumber = "102030",
                    birth = null
                )
            )
        )
    }


    @Test
    fun getContractingPartnerDocumentsLocation() {
        every { getContractingPartnerDocumentsLocationService.getDocumentsLocation(1L) } returns documentsLocation
        Assertions.assertThat(dataProvider.getContractingPartnerDocumentsLocation(1L)).isEqualTo(
            ProjectContractingPartnerDocumentsLocationData(
                id = 18L,
                partnerId = 1L,
                firstName = "Test",
                lastName = "Sample",
                nutsThreeRegionCode = "",
                city = "Istanbul",
                countryCode = "TR",
                nutsTwoRegionCode = "",
                country = "Turkey",
                emailAddress = "sample@mail.com",
                locationNumber = "12A",
                homepage = "homepage",
                institutionName = "Sample name",
                nutsThreeRegion = "",
                nutsTwoRegion = "",
                postalCode = "34000",
                street = "Sample street",
                telephoneNo = "1020304050",
                title = "Title"
            )
        )
    }

    @Test
    fun getContractingPartners() {
        every { versionPersistence.getLatestApprovedOrCurrent(1L) } returns "v1.0"
        every { partnerPersistence.findAllByProjectIdForDropdown(1L, Sort.by(Sort.Order.asc("sortNumber")), "v1.0") } returns
            listOf(
                partnerSummary1,
                partnerSummary2
            )
        Assertions.assertThat(dataProvider.getContractingPartners(1L)).isEqualTo(
            listOf(
                ProjectContractingPartnersSummaryData(
                    id = 99L,
                    abbreviation = "test 1",
                    active = true,
                    role = ProjectPartnerRoleData.LEAD_PARTNER,
                    sortNumber = 1,
                    institutionName = null
                ),
                ProjectContractingPartnersSummaryData(
                    id = 100L,
                    abbreviation = "test 2",
                    active = true,
                    role = ProjectPartnerRoleData.PARTNER,
                    sortNumber = 2,
                    institutionName = null
                )
            )
        )
    }

    @Test
    fun getContractingReporting() {
        every { getContractingReportingService.getReportingSchedule(1L) } returns listOf(
            reportingSchedule
        )
        Assertions.assertThat(dataProvider.getContractingReporting(1L)).isEqualTo(
            listOf(
                ProjectContractingReportingScheduleData(
                    id = 55L,
                    type = ContractingDeadlineTypeData.Content,
                    periodNumber = 1,
                    date = LocalDate.of(2023, 7, 6),
                    comment = "sample comment",
                    number = 1,
                    linkedSubmittedProjectReportNumbers = setOf(),
                    linkedDraftProjectReportNumbers = setOf()
                )
            )
        )
    }

    @Test
    fun getContractingPartnerStateAidDeMinimis() {
        every { getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(1L) } returns stateAidDeMinimisSection
        Assertions.assertThat(dataProvider.getContractingPartnerStateAidDeMinimis(1L)).isEqualTo(
            ProjectContractingPartnerStateAidDeMinimisData(
                partnerId = 1L,
                dateOfGrantingAid = LocalDate.of(2022, 1, 5),
                totalEligibleBudget = BigDecimal.TEN,
                selfDeclarationSubmissionDate = ZonedDateTime.of(2022,1,30,15,10,10,10, ZoneId.systemDefault()),
                baseForGranting = BaseForGrantingData.ADDENDUM_SUBSIDY_CONTRACT,
                aidGrantedByCountryCode = null,
                aidGrantedByCountry = "Österreich",
                memberStatesGranting = setOf(
                    MemberStateForGrantingData(
                        partnerId = 1L,
                        country = "Slovakia",
                        countryCode = "SK",
                        selected = false,
                        amountInEur = BigDecimal.ONE
                    )
                ),
                comment = "Test comment"
            )
        )
    }

    @Test
    fun getContractingPartnerStateAidGber() {
        every { getContractingPartnerStateAidGberService.getGberSection(1L) } returns stateAidGberSection
        Assertions.assertThat(dataProvider.getContractingPartnerStateAidGber(1L)).isEqualTo(
            ProjectContractingPartnerStateAidGberData(
                partnerId = 1L,
                dateOfGrantingAid = LocalDate.of(2022, 1, 5),
                partnerFunds = setOf(
                    PartnerBudgetPerFundData(
                        fund = ProgrammeFundData(id = 1L, selected = true, type = ProgrammeFundTypeData.ERDF),
                        percentage = BigDecimal.ZERO,
                        percentageOfTotal = BigDecimal.ZERO,
                        value = BigDecimal.ZERO
                    )
                ),
                totalEligibleBudget = BigDecimal.TEN,
                naceGroupLevel = NaceGroupLevelData.A_01_1,
                aidIntensity = BigDecimal.TEN,
                locationInAssistedArea = LocationInAssistedAreaData.A_AREA,
                comment = "Test comment"
            )
        )
    }

}
