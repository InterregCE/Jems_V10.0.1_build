package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetContractingPartnerBankingDetailsTest : UnitTest() {

    companion object {
        private const val partnerId = 1L

        private val bankingDetails = ContractingPartnerBankingDetails(
            partnerId = partnerId,
            accountHolder = "Test",
            accountNumber = "123",
            accountIBAN = "RO99BT123",
            accountSwiftBICCode = "MIDT123",
            internalReferenceNr = "irnr",
            bankName = "BT",
            streetName = "Test",
            streetNumber = "42A",
            postalCode = "000123",
            city = "wien",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)"
        )
    }

    @MockK
    private lateinit var bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence

    @InjectMockKs
    private lateinit var interactor: GetContractingPartnerBankingDetails

    @Test
    fun `get banking details - success`() {
        every { bankingDetailsPersistence.getBankingDetails(partnerId) } returns bankingDetails
        assertThat(interactor.getBankingDetails(partnerId)).isEqualTo(bankingDetails)
    }

}
