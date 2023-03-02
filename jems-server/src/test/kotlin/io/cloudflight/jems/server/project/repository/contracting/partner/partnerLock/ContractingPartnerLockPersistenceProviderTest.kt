package io.cloudflight.jems.server.project.repository.contracting.partner.partnerLock

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerLockEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional

internal class ContractingPartnerLockPersistenceProviderTest: UnitTest() {

    companion object {
        const val PROJECT_ID = 21L
        private val lockedPartnerEntities = listOf(
            ProjectContractingPartnerLockEntity(
                partnerId = 73L,
                projectId = PROJECT_ID
            ),
            ProjectContractingPartnerLockEntity(
                partnerId = 75,
                projectId = PROJECT_ID
            )
        )
    }

    @MockK
    lateinit var contractingPartnerLockRepository: ContractingPartnerLockRepository

    @InjectMockKs
    lateinit var contractingPartnerLockPersistenceProvider: ContractingPartnerLockPersistenceProvider


    @Test
    fun `locked partners are fetched and mapped`() {
        every { contractingPartnerLockRepository.findAllByProjectId(PROJECT_ID) } returns lockedPartnerEntities
        assertThat(contractingPartnerLockPersistenceProvider.getLockedPartners(PROJECT_ID)).containsAll(
            setOf(73L, 75L)
        )
    }

    @Test
    fun `check if partner is locked`() {
        every { contractingPartnerLockRepository.findById(73L) } returns Optional.of(mockk())
        assertThat(contractingPartnerLockPersistenceProvider.isLocked(73L)).isTrue
    }
}