package io.cloudflight.jems.server.project.controller.contracting.partner.stateAid

import io.cloudflight.jems.api.project.dto.budget.PartnerBudgetPerFundDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.BaseForGrantingDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.LocationInAssistedAreaDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.MemberStateForGrantingDTO
import io.cloudflight.jems.api.project.dto.partner.NaceGroupLevelDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.BaseForGranting
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.LocationInAssistedArea
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimisInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.updateStateAidDeMinimisSection.UpdateContractingPartnerStateAidDeMinimisInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnersStateAidGberInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.updateStateAidGberSection.UpdateContractingPartnerStateAidGberInteractor
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ContractingPartnerStateAidControllerTest: UnitTest() {
    @MockK
    lateinit var getDeMinimisSectionInteractor: GetContractingPartnerStateAidDeMinimisInteractor

    @MockK
    lateinit var getGberSectionInteractor: GetContractingPartnersStateAidGberInteractor

    @MockK
    lateinit var updateDeMinimisSectionInteractor: UpdateContractingPartnerStateAidDeMinimisInteractor

    @MockK
    lateinit var updateGberSectionInteractor: UpdateContractingPartnerStateAidGberInteractor

    @InjectMockKs
    lateinit var contractingPartnerStateAidController: ContractingPartnerStateAidController

    companion object {
        private const val PARTNER_ID = 2L
        private val localDateNow = LocalDate.now()
        private val zonedTimeNow = ZonedDateTime.now()
        private val fund1 = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF)
        private val fund2 = ProgrammeFund(id = 2L, selected = true, type = ProgrammeFundType.IPA_III)
        private val fund3 = ProgrammeFund(id = 3L, selected = true, type = ProgrammeFundType.OTHER)
        private const val COUNTRY_AT = "Österreich"
        private const val COUNTRY_AT_CODE = "AT"
        private const val COUNTRY_RO = "Romania"
        private const val COUNTRY_RO_CODE = "RO"

        val gberSectionModel = ContractingPartnerStateAidGberSection(
            partnerId = PARTNER_ID,
            dateOfGrantingAid = localDateNow,
            partnerFunds = setOf(
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
            ),
            totalEligibleBudget = BigDecimal.TEN,
            naceGroupLevel = NaceGroupLevel.A,
            aidIntensity = BigDecimal.ONE,
            locationInAssistedArea = LocationInAssistedArea.A_AREA,
            comment = "Comment test"
        )

        val expectedGberSection = ContractingPartnerStateAidGberSectionDTO(
            partnerId = PARTNER_ID,
            dateOfGrantingAid = localDateNow,
            partnerFunds = setOf(
                PartnerBudgetPerFundDTO(
                    fund = fund1.toDto(),
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                ),
                PartnerBudgetPerFundDTO(
                    fund = fund2.toDto(),
                    percentage = BigDecimal(80),
                    percentageOfTotal = BigDecimal(50.85).setScale(2, RoundingMode.HALF_UP),
                    value = BigDecimal(2400).setScale(2)
                ),
                PartnerBudgetPerFundDTO(
                    fund = fund3.toDto(),
                    percentage = BigDecimal.ZERO,
                    percentageOfTotal = BigDecimal.ZERO,
                    value = BigDecimal.ZERO
                )
            ),
            totalEligibleBudget = BigDecimal.TEN,
            naceGroupLevel = NaceGroupLevelDTO.A,
            aidIntensity = BigDecimal.ONE,
            locationInAssistedArea = LocationInAssistedAreaDTO.A_AREA,
            comment = "Comment test"
        )

        private val memberStates = setOf(
            MemberStateForGranting(
                partnerId = PARTNER_ID,
                country = COUNTRY_RO,
                countryCode = COUNTRY_RO_CODE,
                selected = false,
                amountInEur = BigDecimal.ONE
            ),
            MemberStateForGranting(
                partnerId = PARTNER_ID,
                country = COUNTRY_AT,
                countryCode = COUNTRY_AT_CODE,
                selected = true,
                amountInEur = BigDecimal.TEN
            )
        )

        private val memberStatesDTO = setOf(
            MemberStateForGrantingDTO(
                partnerId = PARTNER_ID,
                country = COUNTRY_RO,
                countryCode = COUNTRY_RO_CODE,
                selected = false,
                amountInEur = BigDecimal.ONE
            ),
            MemberStateForGrantingDTO(
                partnerId = PARTNER_ID,
                country = COUNTRY_AT,
                countryCode = COUNTRY_AT_CODE,
                selected = true,
                amountInEur = BigDecimal.TEN
            )
        )

        val deMinimisSectionModel = ContractingPartnerStateAidDeMinimisSection(
            partnerId = PARTNER_ID,
            dateOfGrantingAid = localDateNow,
            totalEligibleBudget = BigDecimal.TEN,
            selfDeclarationSubmissionDate = zonedTimeNow,
            baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
            aidGrantedByCountryCode = COUNTRY_AT_CODE,
            aidGrantedByCountry = COUNTRY_AT,
            memberStatesGranting = memberStates,
            comment = "Test comment"
        )
        val expectedDeMinimisSection = ContractingPartnerStateAidDeMinimisSectionDTO(
            partnerId = PARTNER_ID,
            dateOfGrantingAid = localDateNow,
            totalEligibleBudget = BigDecimal.TEN,
            selfDeclarationSubmissionDate = zonedTimeNow,
            baseForGranting = BaseForGrantingDTO.ADDENDUM_SUBSIDY_CONTRACT,
            aidGrantedByCountryCode = COUNTRY_AT_CODE,
            aidGrantedByCountry = COUNTRY_AT,
            memberStatesGranting = memberStatesDTO,
            comment = "Test comment"
        )

        val deMinimisUpdatedDTO = ContractingPartnerStateAidDeMinimisDTO(
            selfDeclarationSubmissionDate = zonedTimeNow,
            baseForGranting = BaseForGrantingDTO.ADDENDUM_SUBSIDY_CONTRACT,
            aidGrantedByCountryCode = COUNTRY_RO_CODE,
            aidGrantedByCountry = COUNTRY_RO,
            memberStatesGranting = memberStatesDTO,
            comment = "Test comment updated"
        )

        val gberUpdatedDTO = ContractingPartnerStateAidGberDTO(
            aidIntensity = BigDecimal.TEN,
            locationInAssistedArea = LocationInAssistedAreaDTO.NOT_APPLICABLE,
            comment = "Test comment updated"
        )

    }

    @Test
    fun getDeMinimisSection() {
        every { getDeMinimisSectionInteractor.getDeMinimisSection(PARTNER_ID) } returns deMinimisSectionModel

        assertThat( contractingPartnerStateAidController.getDeMinimisSection(PARTNER_ID)).isEqualTo(
            expectedDeMinimisSection
        )
    }

    @Test
    fun updateDeMinimisSection() {
        every { updateDeMinimisSectionInteractor.updateDeMinimisSection(PARTNER_ID, deMinimisUpdatedDTO.toModel()) } returns deMinimisSectionModel

        assertThat( contractingPartnerStateAidController.updateDeMinimisSection(PARTNER_ID, deMinimisUpdatedDTO)).isEqualTo(
            expectedDeMinimisSection
        )
    }

    @Test
    fun getGberSection() {
        every { getGberSectionInteractor.getGberSection(PARTNER_ID) } returns gberSectionModel

        assertThat( contractingPartnerStateAidController.getGberSection(PARTNER_ID)).isEqualTo(
            expectedGberSection
        )
    }

    @Test
    fun updateGberSection() {
        every { updateGberSectionInteractor.updateGberSection(PARTNER_ID, gberUpdatedDTO.toModel()) } returns gberSectionModel

        assertThat( contractingPartnerStateAidController.updateGberSection(PARTNER_ID, gberUpdatedDTO)).isEqualTo(
            expectedGberSection
        )
    }
}
