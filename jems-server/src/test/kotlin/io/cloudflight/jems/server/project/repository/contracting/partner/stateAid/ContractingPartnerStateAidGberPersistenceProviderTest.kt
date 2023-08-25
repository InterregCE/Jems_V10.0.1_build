package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGberEntity
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberPersistenceProvider
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberRepository
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.LocationInAssistedArea
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional

internal class ContractingPartnerStateAidGberPersistenceProviderTest: UnitTest() {

    companion object {
        const val PROJECT_ID = 21L
        const val PARTNER_ID = 1L
        private val gberEntity: ProjectContractingPartnerStateAidGberEntity =
            ProjectContractingPartnerStateAidGberEntity(
                partnerId = PARTNER_ID,
                aidIntensity = BigDecimal.TEN,
                locationInAssistedArea = LocationInAssistedArea.A_AREA,
                comment = "Comment test",
                amountGrantingAid = BigDecimal.TEN
            )


        private val gberModel = ContractingPartnerStateAidGber(
            aidIntensity = BigDecimal.TEN,
            locationInAssistedArea = LocationInAssistedArea.A_AREA,
            comment = "Comment test",
            amountGrantingAid = BigDecimal.TEN
        )
    }

    @MockK
    lateinit var contractingPartnerStateAidGberRepository: ContractingPartnerStateAidGberRepository

    @InjectMockKs
    lateinit var contractingPartnerStateAidGberPersistenceProvider: ContractingPartnerStateAidGberPersistenceProvider

    @Test
    fun `get gber data test`() {
        every { contractingPartnerStateAidGberRepository.findById(PARTNER_ID) } returns Optional.of(gberEntity)

        Assertions.assertThat(contractingPartnerStateAidGberPersistenceProvider.findById(PARTNER_ID)).isEqualTo(
            gberModel
        )
    }

    @Test
    fun `update gber data test`() {
        every { contractingPartnerStateAidGberRepository.save(any()) } returns gberEntity

        Assertions.assertThat(contractingPartnerStateAidGberPersistenceProvider.saveGber(PARTNER_ID, gberModel)).isEqualTo(
            gberEntity
        )
    }
}
