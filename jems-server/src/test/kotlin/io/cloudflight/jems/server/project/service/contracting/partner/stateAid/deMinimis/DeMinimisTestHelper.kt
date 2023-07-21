package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.server.notification.inApp.service.project.ProjectNotificationRecipientServiceTest
import io.cloudflight.jems.server.nuts.entity.NutsCountry
import io.cloudflight.jems.server.nuts.entity.NutsRegion1
import io.cloudflight.jems.server.nuts.entity.NutsRegion2
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGrantedByMemberStateEntity
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidMinimisEntity
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingStateAidGrantedByMemberStateId
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.BaseForGranting
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZonedDateTime

const val PROJECT_ID = 1L
const val PARTNER_ID = 2L
val zonedTimeNow = ZonedDateTime.now()
val date1 = LocalDate.now().plusDays(1)
val date2 = LocalDate.now().plusDays(2)
val date3 = LocalDate.now().plusDays(3)
val fund1 = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF)
val fund2 = ProgrammeFund(id = 2L, selected = true, type = ProgrammeFundType.IPA_III)
val fund3 = ProgrammeFund(id = 3L, selected = true, type = ProgrammeFundType.OTHER)
const val COUNTRY_AT = "Österreich"
const val COUNTRY_AT_CODE = "AT"
const val COUNTRY_SK = "Slovakia"
const val COUNTRY_SK_CODE = "SK"
const val LAST_APPROVED_VERSION = "1.0"
val leadPartner = ProjectPartnerDetail(
    projectId = PROJECT_ID,
    id = 2L,
    active = true,
    abbreviation = "A",
    role = ProjectPartnerRole.LEAD_PARTNER,
    nameInOriginalLanguage = "A",
    nameInEnglish = "A",
    createdAt = ZonedDateTime.now().minusDays(5),
    sortNumber = 1,
    partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
    partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
    nace = NaceGroupLevel.A,
    otherIdentifierNumber = null,
    otherIdentifierDescription = emptySet(),
    pic = null,
    vat = "test vat",
    vatRecovery = ProjectPartnerVatRecovery.Yes,
    legalStatusId = null,
)

val addDates = listOf(
    ProjectContractingMonitoringAddDate(projectId = PROJECT_ID, number = 1, entryIntoForceDate = date1),
    ProjectContractingMonitoringAddDate(projectId = PROJECT_ID, number = 2, entryIntoForceDate = date2),
    ProjectContractingMonitoringAddDate(projectId = PROJECT_ID, number = 3, entryIntoForceDate = date3),
)
val expectedDateOfGrantingAid = addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate

val memberStates = setOf(
    MemberStateForGranting(
        partnerId = PARTNER_ID,
        country = COUNTRY_SK,
        countryCode = COUNTRY_SK_CODE,
        selected = false,
        amountInEur = BigDecimal.ONE
    )
)

val memberStatesEntities = setOf(
    ProjectContractingPartnerStateAidGrantedByMemberStateEntity(
        id = ProjectContractingStateAidGrantedByMemberStateId(PARTNER_ID, COUNTRY_SK_CODE),
        country = COUNTRY_SK,
        selected = false,
        amount = BigDecimal.ONE
    )
)

val expectedMemberStatesForNoData = setOf(
    MemberStateForGranting(
        partnerId = PARTNER_ID,
        country = COUNTRY_SK,
        countryCode = COUNTRY_SK_CODE,
        selected = false,
        amountInEur = BigDecimal.ZERO
    )
)

val expectedMemberStates = setOf(
    MemberStateForGranting(
        partnerId = PARTNER_ID,
        country = COUNTRY_SK,
        countryCode = COUNTRY_SK_CODE,
        selected = false,
        amountInEur = BigDecimal.ONE
    )
)

val expectedEmptyDeMinimisSectionModel = ContractingPartnerStateAidDeMinimisSection(
    partnerId = PARTNER_ID,
    dateOfGrantingAid = expectedDateOfGrantingAid,
    totalEligibleBudget = BigDecimal.TEN,
    selfDeclarationSubmissionDate = null,
    baseForGranting = null,
    aidGrantedByCountryCode = null,
    aidGrantedByCountry = null,
    memberStatesGranting = expectedMemberStatesForNoData,
    comment = null
)

val expectedDeMinimisSection = ContractingPartnerStateAidDeMinimisSection(
    partnerId = PARTNER_ID,
    dateOfGrantingAid = expectedDateOfGrantingAid,
    totalEligibleBudget = BigDecimal.TEN,
    selfDeclarationSubmissionDate = zonedTimeNow,
    baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
    aidGrantedByCountryCode = COUNTRY_AT_CODE,
    aidGrantedByCountry = COUNTRY_AT,
    memberStatesGranting = expectedMemberStates,
    comment = "Test comment"
)

val deMinimisModel = ContractingPartnerStateAidDeMinimis(
    selfDeclarationSubmissionDate = zonedTimeNow,
    baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
    aidGrantedByCountry = COUNTRY_AT,
    aidGrantedByCountryCode = COUNTRY_AT_CODE,
    memberStatesGranting = memberStates,
    comment = "Test comment"
)

val emptyDeMinimisModel = ContractingPartnerStateAidDeMinimis(
    selfDeclarationSubmissionDate = null,
    baseForGranting = null,
    aidGrantedByCountry = null,
    aidGrantedByCountryCode = null,
    memberStatesGranting = emptySet(),
    comment = null
)



val deMinimisEntity = ProjectContractingPartnerStateAidMinimisEntity(
    partnerId = PARTNER_ID,
    selfDeclarationSubmissionDate = zonedTimeNow,
    baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
    aidGrantedByCountry = COUNTRY_AT,
    aidGrantedByCountryCode = COUNTRY_AT_CODE,
    memberStatesGranting = memberStatesEntities,
    comment = "Test comment"
)

fun getContractMonitoring(): ProjectContractingMonitoring {

    val contractMonitoring = mockk<ProjectContractingMonitoring>()
    every { contractMonitoring.addDates } returns addDates

    return contractMonitoring
}

val minimisStateAid = ProgrammeStateAid(
    schemeNumber = "1",
    measure = ProgrammeStateAidMeasure.GENERAL_DE_MINIMIS
)

val gberStateAid = ProgrammeStateAid(
    schemeNumber = "1",
    measure = ProgrammeStateAidMeasure.GBER_ARTICLE_14
)

fun getStateAid(hasRisk: Boolean, stateAidMeasure: ProgrammeStateAid?): ProjectPartnerStateAid {
    val programmeStateAid = mockk<ProjectPartnerStateAid>()

    every { programmeStateAid.stateAidScheme } returns stateAidMeasure

    if (hasRisk) {
        every { programmeStateAid.answer1 } returns true
        every { programmeStateAid.answer2 } returns true
        every { programmeStateAid.answer3 } returns true
        every { programmeStateAid.answer4 } returns true
    } else {
        every { programmeStateAid.answer1 } returns true
        every { programmeStateAid.answer2 } returns true
        every { programmeStateAid.answer3 } returns false
        every { programmeStateAid.answer4 } returns false
    }

    return programmeStateAid
}

fun getPartnerFunds(): Set<PartnerBudgetPerFund> {
    return setOf(
        PartnerBudgetPerFund(
            fund = fund1,
            percentage = BigDecimal.ZERO,
            percentageOfTotal = BigDecimal.ZERO,
            value = BigDecimal.ZERO
        ),
        PartnerBudgetPerFund(
            fund = fund2,
            percentage = BigDecimal(80),
            percentageOfTotal = BigDecimal(50.85).setScale(2, RoundingMode.HALF_UP),
            value = BigDecimal(2400).setScale(2)
        ),
        PartnerBudgetPerFund(
            fund = fund3,
            percentage = BigDecimal.ZERO,
            percentageOfTotal = BigDecimal.ZERO,
            value = BigDecimal.ZERO
        )
    )

}

val partnerSummary = ProjectPartnerSummary(
    id = PARTNER_ID,
    active = true,
    abbreviation = "LP 1",
    role = ProjectPartnerRole.LEAD_PARTNER,
    sortNumber = 1
)

val partnerFunds = listOf(
    ProjectPartnerBudgetPerFund(
        partner = partnerSummary,
        totalEligibleBudget = BigDecimal.TEN,
        budgetPerFund = getPartnerFunds(),
        publicContribution = BigDecimal.ZERO,
        autoPublicContribution = BigDecimal.ZERO,
        privateContribution = BigDecimal.ZERO,
        totalPartnerContribution = BigDecimal.ZERO,
    ),
)

val nuts = NutsRegion3(
    id = "SK010", title = "Slovakia R3",
    region2 = NutsRegion2(
        id = "SK01", title = "Slovakia R2",
        region1 = NutsRegion1(
            id = "SK0", title = "Slovakia R1",
            country = NutsCountry(id = "SK", title = "Slovakia")
        )
    )
)

fun getProgrammeData(): ProgrammeDataEntity {
    val programmeData = mockk<ProgrammeDataEntity>()
    every { programmeData.programmeNuts } returns setOf(nuts)
    return programmeData
}
