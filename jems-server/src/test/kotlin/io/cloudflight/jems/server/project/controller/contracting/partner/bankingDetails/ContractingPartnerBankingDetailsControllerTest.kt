package io.cloudflight.jems.server.project.controller.contracting.partner.bankingDetails

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBankingDetailsDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails.UpdateContractingPartnerBankingDetailsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ContractingPartnerBankingDetailsControllerTest : UnitTest() {

    private val partnerId = 1L
    private val projectId = 1L

    companion object {
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
            nutsThreeRegion = "Wien (AT130)"
        )

        private val bankingDetailsDTO = ContractingPartnerBankingDetailsDTO(
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
            nutsThreeRegion = "Wien (AT130)"
        )
    }

    @MockK
    lateinit var getBankingDetailsInteractor: GetContractingPartnerBankingDetailsInteractor

    @MockK
    lateinit var updateBankingDetailsInteractor: UpdateContractingPartnerBankingDetailsInteractor

    @InjectMockKs
    lateinit var controller: ContractingPartnerBankingDetailsController

    @Test
    fun `get partner contract banking details - success`() {
        every { getBankingDetailsInteractor.getBankingDetails(partnerId) } returns bankingDetails
        Assertions.assertThat(controller.getBankingDetails(projectId, partnerId)).isEqualTo(bankingDetailsDTO)
    }

    @Test
    fun `update partner contract banking details - success`() {
        val updateModelSlot = slot<ContractingPartnerBankingDetails>()

        every {
            updateBankingDetailsInteractor.updateBankingDetails(
                partnerId,
                projectId,
                capture(updateModelSlot)
            )
        } returns bankingDetails

        val toCreate = bankingDetailsDTO
        Assertions.assertThat(controller.updateBankingDetails(projectId, partnerId, toCreate)).isEqualTo(bankingDetailsDTO)
        Assertions.assertThat(updateModelSlot.captured).isEqualTo(bankingDetails)
    }
}