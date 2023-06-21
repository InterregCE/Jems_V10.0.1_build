package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectContractingPartnerAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GetContractingPartnerBankingDetailsServiceTest: UnitTest() {

    companion object {
        private const val partnerId = 1L

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
            internalReferenceNr = "123456",
            city = "Wien"
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectAuthorization: ProjectAuthorization

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence

    @MockK
    lateinit var authorization: ProjectContractingPartnerAuthorization

    @InjectMockKs
    lateinit var getContractingPartnerBankingDetailsService: GetContractingPartnerBankingDetailsService

    @Test
    fun `get banking details - success`() {
        every { authorization.hasViewPermission(partnerId) } returns true
        every { bankingDetailsPersistence.getBankingDetails(partnerId) } returns bankingDetails
        Assertions.assertThat(getContractingPartnerBankingDetailsService.getBankingDetails(partnerId)).isEqualTo(bankingDetails)
    }

    @Test
    fun `get banking details - invalid input`() {
        val invalidPartnerId = -1L

        every { bankingDetailsPersistence.getBankingDetails(invalidPartnerId) } throws GetContractingPartnerBankingDetailsException(RuntimeException())
        assertThrows<GetContractingPartnerBankingDetailsException> {
            getContractingPartnerBankingDetailsService.getBankingDetails(invalidPartnerId)
        }
    }

}
