package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGberEntity
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.LocationInAssistedArea
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate


const val PROJECT_ID = 1L
const val PARTNER_ID = 2L
const val LAST_APPROVED_VERSION = "1.0"
val date1 = LocalDate.now().plusDays(1)
val date2 = LocalDate.now().plusDays(2)
val date3 = LocalDate.now().plusDays(3)
val fund1 = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF)
val fund2 = ProgrammeFund(id = 2L, selected = true, type = ProgrammeFundType.IPA_III)
val fund3 = ProgrammeFund(id = 3L, selected = true, type = ProgrammeFundType.OTHER)

val addDates = listOf(
    ProjectContractingMonitoringAddDate(projectId = PROJECT_ID, number = 1, entryIntoForceDate = date1),
    ProjectContractingMonitoringAddDate(projectId = PROJECT_ID, number = 2, entryIntoForceDate = date2),
    ProjectContractingMonitoringAddDate(projectId = PROJECT_ID, number = 3, entryIntoForceDate = date3),
)
val expectedDateOfGrantingAid = addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate

val expectedEmptyGberSectionModel = ContractingPartnerStateAidGberSection(
    partnerId = PARTNER_ID,
    dateOfGrantingAid = expectedDateOfGrantingAid,
    partnerFunds = getExpectedPartnerFunds(),
    amountGrantingAid = BigDecimal.TEN,
    naceGroupLevel = NaceGroupLevel.A_01_1,
    aidIntensity = BigDecimal.ZERO,
    locationInAssistedArea = null,
    comment = null
)

val expectedGberSection = ContractingPartnerStateAidGberSection(
    partnerId = PARTNER_ID,
    dateOfGrantingAid = expectedDateOfGrantingAid,
    partnerFunds = getExpectedPartnerFunds(),
    amountGrantingAid = BigDecimal.TEN,
    naceGroupLevel = NaceGroupLevel.A_01_1,
    aidIntensity = BigDecimal.TEN,
    locationInAssistedArea = LocationInAssistedArea.A_AREA,
    comment = "Test comment"
)


val gberModel = ContractingPartnerStateAidGber(
    aidIntensity = BigDecimal.TEN,
    locationInAssistedArea = LocationInAssistedArea.A_AREA,
    comment = "Test comment",
    amountGrantingAid = BigDecimal.TEN
)


val emptyGberModel = ContractingPartnerStateAidGber(
    aidIntensity = BigDecimal.ZERO,
    locationInAssistedArea = null,
    comment = null,
    amountGrantingAid = null
)

val gberEntity = ProjectContractingPartnerStateAidGberEntity(
    partnerId = PARTNER_ID,
    aidIntensity = BigDecimal.TEN,
    locationInAssistedArea = LocationInAssistedArea.A_AREA,
    comment = "Test comment",
    amountGrantingAid = BigDecimal.TEN
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


val projectPartnerCofinancing = ProjectPartnerCoFinancing(
    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
    fund = ProgrammeFund(id = 1, type = ProgrammeFundType.ERDF, selected = true),
    percentage = BigDecimal.TEN
)


fun getCofinancing(): ProjectPartnerCoFinancingAndContribution {
    return ProjectPartnerCoFinancingAndContribution(
        finances = listOf(projectPartnerCofinancing),
        partnerContributions = emptyList(),
        partnerAbbreviation = "P1"
    )
}


fun getSpfCofinancing(): ProjectPartnerCoFinancingAndContributionSpf {
    return ProjectPartnerCoFinancingAndContributionSpf(
        finances = listOf(projectPartnerCofinancing),
        partnerContributions = emptyList(),
    )
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


fun getExpectedPartnerFunds(): Set<PartnerBudgetPerFund> {
    return setOf(
        PartnerBudgetPerFund(
            fund = fund1,
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


fun getPartnerData(): ProjectPartnerDetail {
    val partnerData = mockk<ProjectPartnerDetail>()
    every { partnerData.id } returns PARTNER_ID
    every { partnerData.projectId } returns PROJECT_ID
    every { partnerData.nace } returns NaceGroupLevel.A_01_1

    return partnerData
}
