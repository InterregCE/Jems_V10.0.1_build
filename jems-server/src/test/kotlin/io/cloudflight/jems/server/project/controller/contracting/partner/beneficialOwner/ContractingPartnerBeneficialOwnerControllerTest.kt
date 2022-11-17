package io.cloudflight.jems.server.project.controller.contracting.partner.beneficialOwner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBeneficialOwnerDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners.GetContractingPartnerBeneficialOwnersInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwners.UpdateContractingPartnerBeneficialOwnersInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ContractingPartnerBeneficialOwnerControllerTest : UnitTest() {

    companion object {

        private val beneficialOwner = ContractingPartnerBeneficialOwner(
            id = 18L,
            partnerId = 20L,
            firstName = "Test",
            lastName = "Sample",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwnerDTO = ContractingPartnerBeneficialOwnerDTO(
            id = 18L,
            partnerId = 20L,
            firstName = "Test",
            lastName = "Sample",
            vatNumber = "123456",
            birth = null
        )

    }

    @MockK
    lateinit var getBeneficialOwnersInteractor: GetContractingPartnerBeneficialOwnersInteractor

    @MockK
    lateinit var updateBeneficialOwnersInteractor: UpdateContractingPartnerBeneficialOwnersInteractor

    @InjectMockKs
    lateinit var controller: ContractingPartnerBeneficialOwnerController

    @Test
    fun `get contract partner beneficial owners`() {
        val partnerId = 20L
        val projectId = 1L
        every { getBeneficialOwnersInteractor.getBeneficialOwners(partnerId) } returns
                listOf(beneficialOwner)
        Assertions.assertThat(controller.getBeneficialOwners(projectId, partnerId))
            .containsExactly(beneficialOwnerDTO)
    }

    @Test
    fun `update contract partner beneficial owners`() {
        val partnerId = 20L
        val projectId = 1L
        val updateModelSlot = slot<List<ContractingPartnerBeneficialOwner>>()
        every {
            updateBeneficialOwnersInteractor.updateBeneficialOwners(projectId, partnerId, capture(updateModelSlot))
        } returns listOf(beneficialOwner)

        val toCreate = listOf(
            beneficialOwnerDTO
        )
        Assertions.assertThat(controller.updateBeneficialOwners(projectId, partnerId, toCreate))
            .containsExactly(beneficialOwnerDTO)

        Assertions.assertThat(updateModelSlot.captured).hasSize(1)
        Assertions.assertThat(updateModelSlot.captured).containsExactly(beneficialOwner)
    }
}