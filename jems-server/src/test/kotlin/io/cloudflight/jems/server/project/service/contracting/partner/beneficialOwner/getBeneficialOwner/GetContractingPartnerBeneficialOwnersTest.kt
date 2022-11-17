package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwnersPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners.GetContractingPartnerBeneficialOwners
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GetContractingPartnerBeneficialOwnersTest: UnitTest() {

    companion object {
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
    }

    @MockK
    lateinit var beneficialOwnersPersistence: ContractingPartnerBeneficialOwnersPersistence

    @InjectMockKs
    lateinit var interactor: GetContractingPartnerBeneficialOwners

    @Test
    fun `get beneficial owners`() {
        val partnerId = 20L
        every { beneficialOwnersPersistence
            .getBeneficialOwners(partnerId)
        } returns listOf(
            beneficialOwner1, beneficialOwner2
        )
        Assertions.assertThat(interactor.getBeneficialOwners(partnerId))
            .containsExactly(
                beneficialOwner1,
                beneficialOwner2
            )
    }
}
