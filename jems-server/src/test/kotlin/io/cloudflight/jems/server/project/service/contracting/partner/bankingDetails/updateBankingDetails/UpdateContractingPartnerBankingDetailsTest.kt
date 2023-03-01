package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtilService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectContractingPartnerAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectPartnerReportAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateContractingPartnerBankingDetailsTest : UnitTest() {

    private val partnerId = 1L
    private val projectId = 2L

    companion object {
        private val bankingDetailsToBeUpdatedTo = ContractingPartnerBankingDetails(
            partnerId = 1L,
            accountHolder = "Testing",
            accountNumber = "1234",
            accountIBAN = "RO99BT1234",
            accountSwiftBICCode = "MIDT1234",
            bankName = "BTI",
            streetName = "Testing",
            streetNumber = "42B",
            postalCode = "0001243",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)"
        )

        private val invalidBankingDetailsToBeUpdatedTo = ContractingPartnerBankingDetails(
            partnerId = 1L,
            accountHolder = "Testing",
            accountNumber = "1234",
            accountIBAN = "RO99BT1234",
            accountSwiftBICCode = "MIDT1234",
            bankName = "BTI",
            streetName = "Testing",
            streetNumber = "42B",
            postalCode = "000124332543543543",
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
    lateinit var projectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    lateinit var projectPartnerReportAuthorization: ProjectPartnerReportAuthorization

    @MockK
    lateinit var bankingDetailsPersistence: ContractingPartnerBankingDetailsPersistence

    @MockK
    lateinit var authorization: ProjectContractingPartnerAuthorization

    @MockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var interactor: UpdateContractingPartnerBankingDetails

    @Test
    fun `update banking details - success`() {
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every { authorization.hasEditPermission(partnerId) } returns true
        every {
            bankingDetailsPersistence.updateBankingDetails(
                partnerId,
                projectId,
                bankingDetailsToBeUpdatedTo
            )
        } returns bankingDetailsToBeUpdatedTo

        Assertions.assertThat(interactor.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)).isEqualTo(bankingDetailsToBeUpdatedTo)
    }

    @Test
    fun `update banking details - invalid input`() {
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every { authorization.hasEditPermission(partnerId) } returns true
        every {
            bankingDetailsPersistence.updateBankingDetails(
                partnerId,
                projectId,
                invalidBankingDetailsToBeUpdatedTo
            )
        } throws UpdateContractingPartnerBankingDetailsException(RuntimeException())

        assertThrows<UpdateContractingPartnerBankingDetailsException> {
            interactor.updateBankingDetails(partnerId, projectId, invalidBankingDetailsToBeUpdatedTo)
        }
    }

    @Test
    fun `update banking details - unauthorized`() {
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every { authorization.hasEditPermission(partnerId) } returns false
        every {
            bankingDetailsPersistence.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)
        } throws UpdateContractingPartnerBankingDetailsNotAllowedException()

        assertThrows<UpdateContractingPartnerBankingDetailsNotAllowedException> {
            interactor.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)
        }
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
