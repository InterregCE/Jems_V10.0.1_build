package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateContractingPartnerBankingDetailsTest : UnitTest() {

    companion object {
        private const val partnerId = 1L
        private const val projectId = 2L

        private val bankingDetailsToBeUpdatedTo = ContractingPartnerBankingDetails(
            partnerId = partnerId,
            accountHolder = "Testing",
            accountNumber = "1234",
            accountIBAN = "RO99BT1234",
            accountSwiftBICCode = "MIDT1234",
            internalReferenceNr = "intRefNr",
            bankName = "BTI",
            streetName = "Testing",
            streetNumber = "42B",
            postalCode = "0001243",
            city = "WIEN",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)"
        )
    }

    @MockK
    private lateinit var bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence

    @MockK
    private lateinit var validator: ContractingValidator

    @InjectMockKs
    private lateinit var interactor: UpdateContractingPartnerBankingDetails

    @Test
    fun `update banking details - success`() {
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every {
            bankingDetailsPersistence.updateBankingDetails(partnerId, projectId, any())
        } returnsArgument 2

        assertThat(interactor.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)).isEqualTo(bankingDetailsToBeUpdatedTo)
    }

    @Test
    fun `update banking details - section locked`() {
        val exception = ContractingModificationDeniedException()
        every { validator.validatePartnerLock(partnerId) } throws exception

        assertThrows<ContractingModificationDeniedException> {
            interactor.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)
        }
    }

}
