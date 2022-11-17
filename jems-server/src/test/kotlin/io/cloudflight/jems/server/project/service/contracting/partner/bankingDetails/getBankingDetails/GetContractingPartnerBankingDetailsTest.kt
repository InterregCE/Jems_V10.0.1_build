package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtilService
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

internal class GetContractingPartnerBankingDetailsTest : UnitTest() {

    private val partnerId = 1L

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
    lateinit var authorizationUtilService: AuthorizationUtilService

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence

    @InjectMockKs
    lateinit var authorization: ProjectContractingPartnerAuthorization

    @InjectMockKs
    lateinit var interactor: GetContractingPartnerBankingDetails

    @Test
    fun `get banking details - success`() {
        every { authorization.hasViewPermission(partnerId) } returns true
        every { bankingDetailsPersistence.getBankingDetails(partnerId) } returns bankingDetails
        Assertions.assertThat(interactor.getBankingDetails(partnerId)).isEqualTo(bankingDetails)
    }

    @Test
    fun `get banking details - invalid input`() {
        val invalidPartnerId = -1L

        every { authorization.hasViewPermission(invalidPartnerId) } returns true
        every { bankingDetailsPersistence.getBankingDetails(invalidPartnerId) } throws GetContractingPartnerBankingDetailsException(RuntimeException())
        assertThrows<GetContractingPartnerBankingDetailsException> {
            interactor.getBankingDetails(invalidPartnerId)
        }
    }

    @Test
    fun `get banking details - unauthorized`() {
        every { authorization.hasViewPermission(partnerId) } returns false
        every { bankingDetailsPersistence.getBankingDetails(partnerId) } throws GetContractingPartnerBankingDetailsNotAllowedException()
        assertThrows<GetContractingPartnerBankingDetailsNotAllowedException> { interactor.getBankingDetails(partnerId) }
    }
}