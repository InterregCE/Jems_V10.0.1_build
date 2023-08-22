package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidMinimisEntity
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGrantedByMemberStateEntity
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingStateAidGrantedByMemberStateId
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisPersistenceProvider
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisRepository
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.BaseForGranting
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import java.util.Optional

internal class ContractingPartnerStateAidDeMinimisPersistenceProviderTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 21L
        const val PARTNER_ID = 1L
        private val CURRENT_DATE = ZonedDateTime.now()
        private const val COUNTRY_AT = "Österreich"
        private const val COUNTRY_AT_CODE = "AT"
        private const val COUNTRY_RO = "Romania"
        private const val COUNTRY_RO_CODE = "RO"
        private val memberStateEntities = setOf(
            ProjectContractingPartnerStateAidGrantedByMemberStateEntity(
                id = ProjectContractingStateAidGrantedByMemberStateId(
                    partnerId = PARTNER_ID,
                    countryCode = COUNTRY_RO_CODE
                ),
                country = COUNTRY_RO,
                selected = false,
                amount = BigDecimal.ONE
            ),
            ProjectContractingPartnerStateAidGrantedByMemberStateEntity(
                id = ProjectContractingStateAidGrantedByMemberStateId(
                    partnerId = PARTNER_ID,
                    countryCode = COUNTRY_AT_CODE
                ),
                country = COUNTRY_AT,
                selected = true,
                amount = BigDecimal.TEN
            )
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
        private val deMinimisEntity: ProjectContractingPartnerStateAidMinimisEntity =
            ProjectContractingPartnerStateAidMinimisEntity(
                partnerId = PARTNER_ID,
                selfDeclarationSubmissionDate = CURRENT_DATE,
                baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
                aidGrantedByCountry = COUNTRY_AT,
                memberStatesGranting = memberStateEntities,
                comment = "Comment test",
                amountGrantingAid = BigDecimal.TEN
            )


        private val deMinimisModel = ContractingPartnerStateAidDeMinimis(
            selfDeclarationSubmissionDate = CURRENT_DATE,
            baseForGranting = BaseForGranting.ADDENDUM_SUBSIDY_CONTRACT,
            aidGrantedByCountry = COUNTRY_AT,
            memberStatesGranting = memberStates,
            comment = "Comment test",
            amountGrantingAid = BigDecimal.TEN
        )
    }

    @MockK
    lateinit var contractingPartnerStateAidDeMinimisRepository: ContractingPartnerStateAidDeMinimisRepository

    @InjectMockKs
    lateinit var contractingPartnerStateAidDeMinimisPersistenceProvider: ContractingPartnerStateAidDeMinimisPersistenceProvider

    @Test
    fun `get deMinimis data test`() {
        every { contractingPartnerStateAidDeMinimisRepository.findById(PARTNER_ID) } returns Optional.of(deMinimisEntity)

        assertThat(contractingPartnerStateAidDeMinimisPersistenceProvider.findById(PARTNER_ID)).isEqualTo(
            deMinimisModel
        )
    }

    @Test
    fun `save deMinimis data test`() {
        every { contractingPartnerStateAidDeMinimisRepository.save(any()) } returns deMinimisEntity

        assertThat(contractingPartnerStateAidDeMinimisPersistenceProvider.saveDeMinimis(PARTNER_ID, deMinimisModel)).isEqualTo(
            deMinimisEntity
        )
    }
}
