package io.cloudflight.jems.server.project.repository.contracting.partner.beneficialOwner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

internal class ContractingPartnerBeneficialOwnersPersistenceTest: UnitTest() {

    companion object {
        private val beneficialOwner = ContractingPartnerBeneficialOwner(
            id = 18L,
            partnerId = 20L,
            firstName = "Test1",
            lastName = "Sample1",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwnerEntity = ProjectContractingPartnerBeneficialOwnerEntity(
            id = 18L,
            projectPartner = mockk(),
            firstName = "Test1",
            lastName = "Sample1",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwnerEntityToBeDeleted = ProjectContractingPartnerBeneficialOwnerEntity(
            id = 19L,
            projectPartner = mockk(),
            firstName = "Test2",
            lastName = "Sample2",
            vatNumber = "203040",
            birth = null
        )
    }

    @MockK
    lateinit var beneficialOwnersRepository: ContractingPartnerBeneficialOwnersRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @InjectMockKs
    lateinit var persistence: ContractingPartnerBeneficialOwnersPersistenceProvider

    @Test
    fun `get beneficial owners`() {
        val partnerId = 20L
        every { beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId) } returns
            mutableListOf(beneficialOwnerEntity)
        every { beneficialOwnerEntity.projectPartner.id } returns partnerId
        Assertions.assertThat(persistence.getBeneficialOwners(partnerId))
            .containsExactly(beneficialOwner)
    }

    @Test
    fun `update beneficial owners`() {
        val partnerId = 20L
        every { beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId) } returns
            mutableListOf(beneficialOwnerEntity, beneficialOwnerEntityToBeDeleted) andThen
            mutableListOf(beneficialOwnerEntity)

        every { beneficialOwnerEntity.projectPartner.id } returns partnerId
        every { beneficialOwnerEntityToBeDeleted.projectPartner.id } returns partnerId
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(mockk())

        val deletedSlot = slot<Iterable<ProjectContractingPartnerBeneficialOwnerEntity>>()
        every { beneficialOwnersRepository.deleteAll(capture(deletedSlot)) } answers { }
        every { beneficialOwnersRepository.save(any()) } returnsArgument 0

        Assertions.assertThat(persistence.updateBeneficialOwners(partnerId, listOf(beneficialOwner)))
            .containsExactly(beneficialOwner)
        Assertions.assertThat(deletedSlot.captured.map { it.id }).containsExactly(beneficialOwnerEntityToBeDeleted.id)

    }
}
